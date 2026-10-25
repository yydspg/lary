// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;
import io.lark.metric.NullMonitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Executes an {@link OpGraph} on a dedicated pool.
 *
 * <p>Uses a dependency-counter ready queue: an op is submitted only after all
 * of its dependencies completed, and no driver thread is parked per dependency.
 * Timeout support mirrors the Node executor: per-op deadlines, business
 * {@link Op#fallback}, repeated-timeout detection ({@link TimeoutTracker}),
 * quarantine and optional graph abort.
 *
 * <p>A failed op does not poison dependents: its completion still releases
 * downstream ops so they run and can inspect the executor status.
 */
public final class OpExecutor implements AutoCloseable {

    private final ExecutorService pool;
    private final boolean ownsPool;
    private final ScheduledExecutorService timeoutScheduler;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Map<Op, OpStatus> statuses =
            Collections.synchronizedMap(new IdentityHashMap<>());
    private final TimeoutTracker timeoutTracker = new TimeoutTracker();

    private volatile Monitor monitor;
    private volatile TimeoutPolicy timeoutPolicy = TimeoutPolicy.disabled();
    private volatile boolean lastRunAborted;
    private volatile String lastAbortReason = "";

    /** Uses a virtual-thread-per-task dag pool and a null monitor. */
    public OpExecutor() {
        this(createVirtualPool(), true, NullMonitor.INSTANCE);
    }

    /** Uses a virtual-thread-per-task dag pool and the given monitor. */
    public OpExecutor(Monitor monitor) {
        this(createVirtualPool(), true, monitor);
    }

    /** Uses a caller-owned dag pool (not shut down by {@link #close()}). */
    public OpExecutor(ExecutorService dagPool) {
        this(dagPool, false, NullMonitor.INSTANCE);
    }

    public OpExecutor(ExecutorService dagPool, Monitor monitor) {
        this(dagPool, false, monitor);
    }

    private OpExecutor(ExecutorService dagPool, boolean ownsPool, Monitor monitor) {
        this.pool = Objects.requireNonNull(dagPool, "dagPool");
        this.ownsPool = ownsPool;
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor(
                platformFactory("lark-op-timer-"));
        this.monitor = monitor == null ? NullMonitor.INSTANCE : monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor == null ? NullMonitor.INSTANCE : monitor;
    }

    public void setTimeoutPolicy(TimeoutPolicy policy) {
        this.timeoutPolicy = policy == null ? TimeoutPolicy.disabled() : policy;
    }

    public TimeoutPolicy timeoutPolicy() {
        return timeoutPolicy;
    }

    public TimeoutTracker timeoutTracker() {
        return timeoutTracker;
    }

    public boolean lastRunAborted() {
        return lastRunAborted;
    }

    public String lastAbortReason() {
        return lastAbortReason;
    }

    /** Execute the graph and block until every op finished or was skipped. */
    public void execute(OpGraph graph, IContext data) {
        executeAsync(graph, data).join();
    }

    /** Non-blocking: completes when the graph drains. */
    public CompletableFuture<Void> executeAsync(OpGraph graph, IContext data) {
        Objects.requireNonNull(graph, "graph");
        Objects.requireNonNull(data, "data");
        if (closed.get()) {
            throw new IllegalStateException("OpExecutor is closed");
        }

        List<Op> order = graph.compile();
        statuses.clear();
        lastRunAborted = false;
        lastAbortReason = "";

        if (order.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        TimeoutPolicy policy = timeoutPolicy;
        RunState state = new RunState(this, graph, order, data, policy);
        state.runStartNanos = System.nanoTime();
        Thread watchdog = startGraphWatchdog(state, policy);

        state.start();

        CompletableFuture<Void> all = state.allDone();
        all.whenComplete((ignored, error) -> {
            if (watchdog != null) {
                watchdog.interrupt();
            }
        });
        return all;
    }

    // ---- dependency-counter scheduler -----------------------------------

    /**
     * Drain the ready queue. Starting a skipped op completes synchronously and
     * may enqueue dependents, so this loop (rather than recursion) handles long
     * skipped / aborted chains without growing the stack.
     */
    private void pump(RunState state) {
        if (!state.pumping.compareAndSet(false, true)) {
            return;
        }
        try {
            while (true) {
                Op op = state.ready.poll();
                if (op == null) {
                    state.pumping.set(false);
                    // Close the enqueue-during-drain race: a producer that saw
                    // pumping == true will not pump again.
                    if (state.ready.isEmpty()
                            || !state.pumping.compareAndSet(false, true)) {
                        return;
                    }
                    continue;
                }
                startOp(op, state);
            }
        } catch (Throwable fatal) {
            state.pumping.set(false);
            state.abort("scheduler failure: " + describeError(fatal));
            failRemaining(state, fatal);
        }
    }

    private void startOp(Op op, RunState state) {
        try {
            startOpInternal(op, state);
        } catch (Throwable fatal) {
            finishFailure(op, state, fatal, 0);
        }
    }

    private void startOpInternal(Op op, RunState state) {
        IContext data = state.data;
        TimeoutPolicy policy = state.policy;
        state.markStarted(op, System.nanoTime() - state.runStartNanos);

        if (state.aborted()) {
            finishSkipped(op, state, state.abortReason());
            return;
        }

        setStatus(op, OpStatus.RUNNING);
        emit("op.start", op.name(), true, 0, state.startedAt(op), Map.of());

        try {
            for (OpAspect aspect : state.graph.aspects()) {
                if (aspect.shouldSkip(op, data)) {
                    finishSkipped(op, state, "aspect");
                    return;
                }
            }
            for (OpAspect aspect : state.graph.aspects()) {
                aspect.onBefore(op, data);
            }
        } catch (Throwable error) {
            finishFailure(op, state, error, 0);
            return;
        }

        String key = op.name();
        if (policy.quarantineEnabled() && timeoutTracker.isQuarantined(key)) {
            handleQuarantined(state.graph, op, data, state, key);
            return;
        }

        Duration effective = effectiveTimeout(op, policy, state);
        if (state.aborted()) {
            finishSkipped(op, state, state.abortReason());
            return;
        }

        long start = System.nanoTime();
        CompletableFuture<Void> body;
        try {
            body = CompletableFuture.runAsync(() -> op.execute(data), pool);
        } catch (Throwable fatal) {
            finishStartFailure(op, state, fatal, start);
            return;
        }
        state.registerRunning(op, body);

        ScheduledFuture<?> timer = null;
        if (!effective.isZero()) {
            try {
                timer = timeoutScheduler.schedule(
                        () -> Thread.ofVirtual()
                                .name("lark-op-timeout-" + op.name())
                                .start(() -> handleTimeoutSignal(op, state, key,
                                        start, effective, body)),
                        effective.toNanos(), TimeUnit.NANOSECONDS);
            } catch (Throwable fatal) {
                body.cancel(true);
                state.unregisterRunning(op);
                finishStartFailure(op, state, fatal, start);
                return;
            }
        }
        final ScheduledFuture<?> timeoutTask = timer;

        body.whenComplete((ignored, error) -> {
            if (timeoutTask != null) {
                timeoutTask.cancel(false);
            }
            state.unregisterRunning(op);
            if (!state.markCompleted(op)) {
                return;
            }
            if (state.aborted()) {
                finishAbortedClaimed(op, state, start);
                return;
            }
            try {
                handleCompletion(state.graph, op, data, state, error,
                        Math.max(0, System.nanoTime() - start));
            } finally {
                finishClaimed(op, state);
            }
        });
    }

    private void handleTimeoutSignal(Op op, RunState state, String key,
                                     long start, Duration timeout,
                                     CompletableFuture<Void> body) {
        if (!state.markCompleted(op)) {
            return;
        }
        state.unregisterRunning(op);
        if (state.policy().interruptOnTimeout()) {
            body.cancel(true);
        }

        long elapsed = Math.max(0, System.nanoTime() - start);
        DagTimeoutException error = new DagTimeoutException(
                op.name(), timeout, false);
        try {
            handleTimeout(state.graph, op, state.data, state, key,
                    elapsed, timeout, error);
        } finally {
            finishClaimed(op, state);
        }
    }

    private void finishSkipped(Op op, RunState state, String reason) {
        if (!state.markCompleted(op)) {
            return;
        }
        setStatus(op, OpStatus.SKIPPED);
        emit("op.skipped", op.name(), true, 0, state.startedAt(op),
                Map.of("reason", reason == null ? "" : reason));
        finishClaimed(op, state);
    }

    private void finishAbortedClaimed(Op op, RunState state, long start) {
        long elapsed = start == 0
                ? 0 : Math.max(0, System.nanoTime() - start);
        setStatus(op, OpStatus.SKIPPED);
        emit("op.skipped", op.name(), true, elapsed,
                state.startedAt(op), Map.of("reason", state.abortReason()));
        finishClaimed(op, state);
    }

    private void finishFailure(Op op, RunState state, Throwable fatal,
                               long start) {
        if (!state.markCompleted(op)) {
            return;
        }
        long elapsed = start == 0
                ? 0 : Math.max(0, System.nanoTime() - start);
        setStatus(op, OpStatus.FAILED);
        emit("op.error", op.name(), false, elapsed,
                state.startedAt(op), Map.of("error", describeError(fatal)));
        finishClaimed(op, state);
    }

    /**
     * The op body could not be scheduled (rejected pool or timer shutdown).
     * Route it through normal completion so business fallback still applies.
     */
    private void finishStartFailure(Op op, RunState state, Throwable fatal,
                                    long start) {
        if (!state.markCompleted(op)) {
            return;
        }
        try {
            handleCompletion(state.graph, op, state.data, state, fatal,
                    Math.max(0, System.nanoTime() - start));
        } finally {
            finishClaimed(op, state);
        }
    }

    private void finishClaimed(Op op, RunState state) {
        state.releaseDependents(op);
        state.completeOne();
        pump(state);
    }

    private void failRemaining(RunState state, Throwable fatal) {
        for (Op op : state.ops()) {
            if (!state.markCompleted(op)) {
                continue;
            }
            setStatus(op, OpStatus.FAILED);
            emit("op.error", op.name(), false, 0,
                    state.startedAt(op), Map.of("error", describeError(fatal)));
            state.completeOne();
        }
    }

    private void handleTimeout(OpGraph graph, Op op, IContext data,
                               RunState state,
                               String key, long elapsed, Duration timeout,
                               Throwable error) {
        TimeoutTracker.Entry entry = timeoutTracker.recordTimeout(key,
                state.policy());

        // The per-op deadline may be the graph deadline itself. If the op
        // timer wins the race against the watchdog, still mark the run aborted.
        if (graphDeadlineReached(state)) {
            state.abort("graph timeout after " + state.policy().graphTimeout());
        }
        Map<String, String> attrs = timeoutAttrs(timeout, entry);

        boolean recovered = safeFallback(op, data, error);
        OpStatus outcome = recovered ? OpStatus.FALLBACK : OpStatus.TIMED_OUT;
        setStatus(op, outcome);
        if (recovered) {
            emit("op.fallback", op.name(), true, elapsed,
                    state.startedAt(op), attrs);
        } else {
            emit("op.timeout", op.name(), false, elapsed,
                    state.startedAt(op), attrs);
        }
        notifyAfter(graph, op, data, outcome);

        if (entry.quarantined()) {
            // The timeout/fallback event above already carries the terminal
            // status; keep this informational event from double-counting stats.
            Map<String, String> quarantinedAttrs = new LinkedHashMap<>(attrs);
            quarantinedAttrs.put("status", "quarantined");
            emit("op.quarantined", op.name(), true, elapsed,
                    state.startedAt(op), quarantinedAttrs);
            if (state.policy().repeatedAction() == TimeoutAction.ABORT_GRAPH) {
                state.abort("op '" + op.name() + "' repeatedly timed out");
            }
        }
    }

    private void handleQuarantined(OpGraph graph, Op op, IContext data,
                                   RunState state, String key) {
        if (!state.markCompleted(op)) {
            return;
        }
        try {
            TimeoutTracker.Entry entry = timeoutTracker.snapshot(key);
            DagTimeoutException error = new DagTimeoutException(
                    op.name(), Duration.ZERO, true);
            boolean recovered = safeFallback(op, data, error);
            OpStatus outcome = recovered ? OpStatus.FALLBACK : OpStatus.SKIPPED;
            setStatus(op, outcome);
            Map<String, String> attrs = timeoutAttrs(Duration.ZERO, entry);
            emit("op.quarantined", op.name(),
                    recovered, 0, state.startedAt(op), attrs);
            notifyAfter(graph, op, data, outcome);

            if (state.policy().repeatedAction() == TimeoutAction.ABORT_GRAPH) {
                state.abort("op '" + op.name() + "' is quarantined");
            }
        } finally {
            finishClaimed(op, state);
        }
    }

    private void handleCompletion(OpGraph graph, Op op, IContext data,
                                  RunState state, Throwable error,
                                  long elapsed) {
        OpStatus outcome;
        if (error == null) {
            timeoutTracker.recordSuccess(op.name());
            outcome = OpStatus.SUCCESS;
            emit("op.end", op.name(), true, elapsed,
                    state.startedAt(op), Map.of());
        } else {
            boolean recovered = safeFallback(op, data, error);
            outcome = recovered ? OpStatus.FALLBACK : OpStatus.FAILED;
            if (recovered) {
                emit("op.fallback", op.name(), true, elapsed,
                        state.startedAt(op), Map.of());
            } else {
                emit("op.error", op.name(), false, elapsed,
                        state.startedAt(op),
                        Map.of("error", describeError(error)));
            }
        }
        setStatus(op, outcome);
        notifyAfter(graph, op, data, outcome);
    }

    private void notifyAfter(OpGraph graph, Op op, IContext data,
                             OpStatus outcome) {
        try {
            for (OpAspect aspect : graph.aspects()) {
                aspect.onAfter(op, data, outcome);
            }
        } catch (Throwable ignored) {
            // Aspect failures must not prevent graph completion.
        }
    }

    private static boolean safeFallback(Op op, IContext data, Throwable error) {
        try {
            return op.fallback(data, error);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Duration effectiveTimeout(Op op, TimeoutPolicy policy,
                                      RunState state) {
        Duration opTimeout = op.timeout();
        Duration base = opTimeout != null && !opTimeout.isZero()
                ? opTimeout : policy.defaultTimeout();

        if (policy.graphTimeout().isZero()) {
            return base;
        }

        long remaining = policy.graphTimeout().toNanos()
                - (System.nanoTime() - state.runStartNanos);
        if (remaining <= 0) {
            state.abort("graph timeout after " + policy.graphTimeout());
            return Duration.ZERO;
        }
        Duration graphRemaining = Duration.ofNanos(remaining);
        if (base.isZero() || graphRemaining.compareTo(base) < 0) {
            return graphRemaining;
        }
        return base;
    }

    private static boolean graphDeadlineReached(RunState state) {
        Duration graphTimeout = state.policy().graphTimeout();
        return !graphTimeout.isZero()
                && System.nanoTime() - state.runStartNanos
                        >= graphTimeout.toNanos();
    }

    private static Map<String, String> timeoutAttrs(Duration timeout,
                                                    TimeoutTracker.Entry entry) {
        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("timeout_ms", Long.toString(timeout.toMillis()));
        attrs.put("consecutive_timeouts",
                Integer.toString(entry.consecutiveTimeouts()));
        attrs.put("total_timeouts", Long.toString(entry.totalTimeouts()));
        attrs.put("quarantined", Boolean.toString(entry.quarantined()));
        if (entry.quarantined()) {
            attrs.put("quarantine_until_ns",
                    Long.toString(entry.quarantineUntilNanos()));
        }
        return attrs;
    }

    private static Thread startGraphWatchdog(RunState state,
                                             TimeoutPolicy policy) {
        if (policy.graphTimeout().isZero()) {
            return null;
        }
        Duration timeout = policy.graphTimeout();
        return Thread.ofVirtual().name("lark-op-graph-timeout").start(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ignored) {
                return;
            }
            state.abort("graph timeout after " + timeout);
        });
    }

    /** Per-op status of the last execution. */
    public OpStatus status(Op op) {
        return statuses.getOrDefault(op, OpStatus.PENDING);
    }

    public boolean allSucceeded() {
        synchronized (statuses) {
            for (OpStatus status : statuses.values()) {
                if (status != OpStatus.SUCCESS
                        && status != OpStatus.SKIPPED
                        && status != OpStatus.FALLBACK) {
                    return false;
                }
            }
            return true;
        }
    }

    private void setStatus(Op op, OpStatus status) {
        statuses.put(op, status);
    }

    private void emit(String action, String name, boolean ok,
                      long elapsedNanos, long startedAtNanos,
                      Map<String, String> attrs) {
        Monitor current = monitor;
        if (current == null || current == NullMonitor.INSTANCE) {
            return;
        }
        try {
            Event event = new Event("dag", action, name)
                    .attrNanos("started_ns", Math.max(0, startedAtNanos))
                    .durationNanos(Math.max(0, elapsedNanos))
                    .ok(ok);
            String status = statusOf(action, ok);
            if (status != null) {
                event.attr("type", "op").attr("status", status);
            }
            for (Map.Entry<String, String> attr : attrs.entrySet()) {
                event.attr(attr.getKey(), attr.getValue());
            }
            current.emit(event);
        } catch (Throwable ignored) {
            // Monitoring must never break graph execution.
        }
    }

    private static String statusOf(String action, boolean ok) {
        return switch (action) {
            case "op.start" -> "running";
            case "op.end" -> "success";
            case "op.error" -> "failed";
            case "op.fallback" -> "fallback";
            case "op.timeout" -> "timed_out";
            case "op.skipped" -> "skipped";
            case "op.quarantined" -> ok ? "fallback" : "skipped";
            default -> null;
        };
    }

    private static Throwable unwrap(Throwable error) {
        Throwable current = error;
        while ((current instanceof CompletionException
                || current instanceof ExecutionException)
                && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private static String describeError(Throwable error) {
        if (error == null) {
            return "";
        }
        String message = error.getMessage();
        return message == null || message.isEmpty()
                ? error.getClass().getName() : message;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            if (ownsPool) {
                pool.shutdownNow();
            }
            timeoutScheduler.shutdownNow();
        }
    }

    private static ExecutorService createVirtualPool() {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("lark-op-", 0).factory());
    }

    private static ThreadFactory platformFactory(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return runnable -> {
            Thread thread = new Thread(runnable,
                    prefix + sequence.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    private static final class RunState {
        private final OpExecutor owner;
        private final OpGraph graph;
        private final IContext data;
        private final TimeoutPolicy policy;
        private final Op[] ops;
        private final IdentityHashMap<Op, Integer> index = new IdentityHashMap<>();
        private final AtomicIntegerArray pending;
        private final int[][] dependentIndices;
        private final ConcurrentLinkedQueue<Op> ready =
                new ConcurrentLinkedQueue<>();
        private final AtomicBoolean pumping = new AtomicBoolean();
        private final Set<Op> completed = ConcurrentHashMap.newKeySet();
        private final AtomicInteger remaining;
        private final CompletableFuture<Void> allDone = new CompletableFuture<>();
        private final AtomicBoolean aborted = new AtomicBoolean();
        private final Map<Op, Future<?>> running = new IdentityHashMap<>();
        private final Map<Op, Long> startedAt = new IdentityHashMap<>();
        private volatile String abortReason = "";
        private long runStartNanos;

        private RunState(OpExecutor owner, OpGraph graph, List<Op> order,
                         IContext data, TimeoutPolicy policy) {
            this.owner = owner;
            this.graph = graph;
            this.data = data;
            this.policy = policy;
            this.ops = order.toArray(Op[]::new);
            int count = ops.length;
            this.pending = new AtomicIntegerArray(count);
            this.remaining = new AtomicInteger(count);

            for (int i = 0; i < count; i++) {
                index.put(ops[i], i);
                pending.set(i, graph.depsOf(ops[i]).size());
            }

            int[] outDegree = new int[count];
            for (int i = 0; i < count; i++) {
                for (Op dependency : graph.depsOf(ops[i])) {
                    outDegree[indexOf(dependency)]++;
                }
            }
            this.dependentIndices = new int[count][];
            for (int i = 0; i < count; i++) {
                dependentIndices[i] = new int[outDegree[i]];
            }
            int[] cursor = new int[count];
            for (int i = 0; i < count; i++) {
                for (Op dependency : graph.depsOf(ops[i])) {
                    int dependencyIndex = indexOf(dependency);
                    dependentIndices[dependencyIndex][cursor[dependencyIndex]++] = i;
                }
            }

            for (int i = 0; i < count; i++) {
                if (pending.get(i) == 0) {
                    ready.add(ops[i]);
                }
            }
        }

        private int indexOf(Op op) {
            Integer value = index.get(op);
            if (value == null) {
                throw new IllegalStateException(
                        "OpExecutor: dependency outside this graph: " + op.name());
            }
            return value;
        }

        private void start() {
            owner.pump(this);
        }

        private TimeoutPolicy policy() {
            return policy;
        }

        private Op[] ops() {
            return ops;
        }

        private void markStarted(Op op, long offsetNanos) {
            synchronized (startedAt) {
                startedAt.put(op, Math.max(0, offsetNanos));
            }
        }

        private long startedAt(Op op) {
            synchronized (startedAt) {
                return startedAt.getOrDefault(op, 0L);
            }
        }

        private boolean markCompleted(Op op) {
            return completed.add(op);
        }

        private void releaseDependents(Op op) {
            for (int dependentIndex : dependentIndices[indexOf(op)]) {
                if (pending.decrementAndGet(dependentIndex) == 0) {
                    ready.add(ops[dependentIndex]);
                }
            }
        }

        private void registerRunning(Op op, Future<?> future) {
            synchronized (running) {
                running.put(op, future);
                if (aborted.get()) {
                    future.cancel(true);
                }
            }
        }

        private void unregisterRunning(Op op) {
            synchronized (running) {
                running.remove(op);
            }
        }

        private void abort(String reason) {
            if (!aborted.compareAndSet(false, true)) {
                return;
            }
            abortReason = reason;
            List<Future<?>> futures;
            synchronized (running) {
                futures = new ArrayList<>(running.values());
            }
            for (Future<?> future : futures) {
                future.cancel(true);
            }
        }

        private boolean aborted() {
            return aborted.get();
        }

        private String abortReason() {
            return abortReason;
        }

        private void completeOne() {
            if (remaining.decrementAndGet() == 0) {
                // Publish the run outcome before releasing joiners, so
                // execute() cannot observe a stale lastRunAborted flag.
                owner.lastRunAborted = aborted.get();
                owner.lastAbortReason = abortReason;
                allDone.complete(null);
            }
        }

        private CompletableFuture<Void> allDone() {
            return allDone;
        }
    }
}
