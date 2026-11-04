// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;
import io.lark.metric.NullMonitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Drives a {@link Graph} to completion with a dependency-counter scheduler.
 *
 * <p>The executor does not create one dependency-waiting virtual thread per
 * node. Instead it precomputes indegrees from the graph, starts only nodes whose
 * dependencies have completed, and propagates completion to dependents. Node
 * bodies still run on their own virtual threads, so blocking business code is
 * cheap and independent nodes run concurrently.
 *
 * <p>Timeout support (opt-in through {@link #setTimeoutPolicy}):
 *
 * <ul>
 *   <li>per-node / per-run deadlines;
 *   <li>the body is interrupted on timeout, then business
 *       {@link Node#fallback} runs;
 *   <li>{@link TimeoutTracker} detects repeated timeouts by node type;
 *   <li>repeated offenders can be quarantined or abort the whole graph run.
 * </ul>
 *
 * <p>Three pools are exposed to business code:
 *
 * <ul>
 *   <li><b>Compute</b> — fixed platform pool (default: CPU cores) for
 *       CPU-bound work. Use {@code Schedule.run(ctx, PoolKind.COMPUTE, ...)}.
 *   <li><b>IO</b> — virtual-thread executor (default parallelism hint:
 *       2 × CPUs) for blocking network/disk work.
 *   <li><b>Background</b> — virtual-thread executor (default hint: 2) for
 *       logging, metrics and other low-priority work.
 * </ul>
 *
 * <p>{@link #execute} blocks the calling thread until every node finished.
 */
public final class Executor implements AutoCloseable {

    private final int computeThreads;
    private final int ioParallelism;
    private final int backgroundParallelism;

    private final ExecutorService computePool;
    private final ExecutorService ioPool;
    private final ExecutorService backgroundPool;
    private final ExecutorService nodeBodyPool;
    private final ScheduledExecutorService timeoutScheduler;

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final TimeoutTracker timeoutTracker = new TimeoutTracker();
    private volatile Monitor monitor = NullMonitor.INSTANCE;
    private volatile TimeoutPolicy timeoutPolicy = TimeoutPolicy.disabled();
    private volatile boolean lastRunAborted;
    private volatile String lastAbortReason = "";

    public Executor() {
        this(0, 0, 0);
    }

    public Executor(int computeThreads, int ioThreads) {
        this(computeThreads, ioThreads, 0);
    }

    /**
     * @param computeThreads fixed compute workers; {@code <= 0} means CPU cores
     * @param ioThreads IO parallelism hint; {@code <= 0} means 2 × CPU cores
     * @param backgroundThreads background parallelism hint; {@code <= 0} means 2
     */
    public Executor(int computeThreads, int ioThreads, int backgroundThreads) {
        int cores = Math.max(1, Runtime.getRuntime().availableProcessors());
        this.computeThreads = computeThreads > 0 ? computeThreads : cores;
        this.ioParallelism = ioThreads > 0 ? ioThreads : 2 * cores;
        this.backgroundParallelism = backgroundThreads > 0 ? backgroundThreads : 2;

        this.computePool = Executors.newFixedThreadPool(
                this.computeThreads, platformFactory("lark-compute-"));
        this.ioPool = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("lark-io-", 0).factory());
        this.backgroundPool = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("lark-bg-", 0).factory());
        this.nodeBodyPool = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("lark-node-body-", 0).factory());
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor(
                platformFactory("lark-dag-timer-"));
    }

    /** Optional observability hook; any thread-safe {@link Monitor}. */
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor == null ? NullMonitor.INSTANCE : monitor;
    }

    /** Enable / configure timeouts. Null restores the disabled policy. */
    public void setTimeoutPolicy(TimeoutPolicy policy) {
        this.timeoutPolicy = policy == null ? TimeoutPolicy.disabled() : policy;
    }

    public TimeoutPolicy timeoutPolicy() {
        return timeoutPolicy;
    }

    /** Persistent repeated-timeout detector; may be reset between runs. */
    public TimeoutTracker timeoutTracker() {
        return timeoutTracker;
    }

    /** Whether the most recent execute() was aborted by the timeout policy. */
    public boolean lastRunAborted() {
        return lastRunAborted;
    }

    public String lastAbortReason() {
        return lastAbortReason;
    }

    public int computeWorkerCount() {
        return computeThreads;
    }

    /** Configured IO parallelism hint (IO executes on virtual threads). */
    public int ioWorkerCount() {
        return ioParallelism;
    }

    /** Configured background parallelism hint (background uses virtual threads). */
    public int backgroundWorkerCount() {
        return backgroundParallelism;
    }

    /** Run the graph to completion (blocking). */
    public void execute(Graph graph, IContext ctx) {
        execute(graph, ctx, Set.of());
    }

    /**
     * Run the graph to completion (blocking).
     *
     * <p>{@code disabledIds} is the batch-disable set: listed nodes are not
     * executed for this run ({@link NodeStatus#SKIPPED}); their completion
     * events fire immediately so downstream nodes and graph completion are
     * unaffected.
     */
    public void execute(Graph graph, IContext ctx, Collection<String> disabledIds) {
        Objects.requireNonNull(graph, "graph");
        Objects.requireNonNull(ctx, "ctx");
        if (closed.get()) {
            throw new IllegalStateException("Executor is closed");
        }

        graph.resetRunState();
        lastRunAborted = false;
        lastAbortReason = "";
        if (graph.isEmpty()) {
            return;
        }

        installPoolsInto(ctx);

        TimeoutPolicy policy = timeoutPolicy;
        RunState state = new RunState(graph, disabledIds, this, ctx, policy);
        state.runStartNanos = System.nanoTime();
        Thread watchdog = startGraphWatchdog(state, policy);

        try {
            state.start();
            state.await();
        } finally {
            if (watchdog != null) {
                watchdog.interrupt();
            }
        }

        lastRunAborted = state.aborted();
        lastAbortReason = state.abortReason();
    }

    // ---- dependency-counter scheduler -----------------------------------

    /**
     * Drain the ready queue. Starting a skipped node completes synchronously
     * and may enqueue dependents, so this loop (rather than recursion) handles
     * long disabled / aborted chains without growing the stack.
     */
    private void pump(RunState state) {
        if (!state.pumping.compareAndSet(false, true)) {
            return;
        }
        try {
            while (true) {
                Node node = state.ready.poll();
                if (node == null) {
                    state.pumping.set(false);
                    // Close the enqueue-during-drain race: a producer that saw
                    // pumping == true will not pump again.
                    if (state.ready.isEmpty()
                            || !state.pumping.compareAndSet(false, true)) {
                        return;
                    }
                    continue;
                }
                startNode(node, state);
            }
        } catch (Throwable fatal) {
            state.pumping.set(false);
            state.abort("scheduler failure: " + describeError(fatal));
        }
    }

    private void startNode(Node node, RunState state) {
        try {
            startNodeInternal(node, state);
        } catch (Throwable fatal) {
            finishFailure(node, state, fatal, 0);
        }
    }

    private void startNodeInternal(Node node, RunState state) {
        IContext ctx = state.ctx;
        TimeoutPolicy policy = state.policy;

        if (state.aborted()) {
            finishSkipped(node, state,
                    new GraphAbortedException(state.abortReason()), 0,
                    state.abortReason());
            return;
        }
        if (state.disabled(node)) {
            finishSkipped(node, state, null, 0, "batch-disabled");
            return;
        }

        long start = System.nanoTime();
        node.setStartedAtNanos(start - state.runStartNanos);
        node.setStatus(NodeStatus.RUNNING);
        emit(node, "node.start", NodeStatus.RUNNING.displayName(),
                -1, node.startedAtNanos(), null, Map.of());

        String key = timeoutKey(node);
        if (policy.quarantineEnabled() && timeoutTracker.isQuarantined(key)) {
            handleQuarantined(node, state, key, start);
            return;
        }

        Duration effective = effectiveTimeout(node, policy, state);
        if (state.aborted()) {
            finishSkipped(node, state,
                    new GraphAbortedException(state.abortReason()),
                    System.nanoTime() - start, state.abortReason());
            return;
        }

        NodeTask body = new NodeTask(node, state, start);
        try {
            nodeBodyPool.execute(body);
        } catch (Throwable fatal) {
            finishFailure(node, state, fatal, start);
            return;
        }
        state.registerRunning(node, body);

        if (!effective.isZero()) {
            try {
                ScheduledFuture<?> timer = timeoutScheduler.schedule(
                        () -> Thread.ofVirtual()
                                .name("lark-node-timeout-" + node.id())
                                .start(() -> handleTimeoutSignal(node, state,
                                        key, start, effective)),
                        effective.toNanos(), TimeUnit.NANOSECONDS);
                state.registerTimer(node, timer);
            } catch (Throwable fatal) {
                body.cancel(true);
                finishFailure(node, state, fatal, start);
                return;
            }
        }
    }

    /**
     * One node body submitted to the node pool.
     *
     * <p>{@link FutureTask#done()} runs for normal completion, failure and
     * cancellation — including a cancellation that wins before the body
     * starts. That guarantees every submitted node claims its terminal state
     * exactly once, so an abort or timeout can never strand the graph.
     */
    private final class NodeTask extends FutureTask<Void> {

        private final Node node;
        private final RunState state;
        private final long start;

        private NodeTask(Node node, RunState state, long start) {
            super(() -> {
                node.run(state.ctx);
                return null;
            });
            this.node = node;
            this.state = state;
            this.start = start;
        }

        @Override
        protected void done() {
            state.cancelTimer(node);
            state.unregisterRunning(node);
            if (!state.markCompleted(node)) {
                return;
            }
            if (state.aborted()) {
                finishAbortedClaimed(node, state, start);
                return;
            }
            Throwable error = null;
            try {
                get();
            } catch (CancellationException cancelled) {
                error = cancelled;
            } catch (ExecutionException failed) {
                error = failed.getCause();
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                error = interrupted;
            }
            completeBody(node, state, error, start);
        }
    }

    private void completeBody(Node node, RunState state, Throwable error,
                              long start) {
        long elapsed = Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        String key = timeoutKey(node);
        timeoutTracker.recordSuccess(key);

        Throwable cause = unwrap(error);
        if (cause == null) {
            node.setStatus(NodeStatus.SUCCESS);
            emit(node, "node.success", NodeStatus.SUCCESS.displayName(),
                    elapsed, node.startedAtNanos(), null, Map.of());
        } else {
            node.setError(cause);
            boolean recovered = safeFallback(node, state.ctx, cause);
            if (recovered) {
                node.setStatus(NodeStatus.FALLBACK);
                emit(node, "node.fallback", NodeStatus.FALLBACK.displayName(),
                        elapsed, node.startedAtNanos(), null, Map.of());
            } else {
                node.setStatus(NodeStatus.FAILED);
                emit(node, "node.failure", NodeStatus.FAILED.displayName(),
                        elapsed, node.startedAtNanos(),
                        describeError(cause), Map.of());
            }
        }
        finishClaimed(node, state);
    }

    private void handleTimeoutSignal(Node node, RunState state, String key,
                                     long start, Duration timeout) {
        if (!state.markCompleted(node)) {
            return;
        }
        state.cancelTimer(node);
        if (state.policy.interruptOnTimeout()) {
            state.cancelRunning(node);
        }

        long elapsed = Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        DagTimeoutException error = new DagTimeoutException(
                node.id(), timeout, false);
        node.setError(error);
        handleTimeout(node, state, key, elapsed, timeout, error);
        finishClaimed(node, state);
    }

    private void finishFailure(Node node, RunState state, Throwable fatal,
                               long start) {
        if (!state.markCompleted(node)) {
            return;
        }
        long elapsed = start == 0
                ? 0 : Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        node.setError(unwrap(fatal));
        node.setStatus(NodeStatus.FAILED);
        emit(node, "node.failure", NodeStatus.FAILED.displayName(),
                elapsed, node.startedAtNanos(), describeError(fatal),
                Map.of());
        finishClaimed(node, state);
    }

    private void finishSkipped(Node node, RunState state, Throwable error,
                               long elapsedNanos, String reason) {
        if (!state.markCompleted(node)) {
            return;
        }
        node.setStatus(NodeStatus.SKIPPED);
        node.setError(error);
        node.setElapsedNanos(Math.max(0, elapsedNanos));
        emit(node, "node.skipped", NodeStatus.SKIPPED.displayName(),
                node.elapsedNanos(), node.startedAtNanos(), reason, Map.of());
        finishClaimed(node, state);
    }

    private void finishAbortedClaimed(Node node, RunState state, long start) {
        long elapsed = Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        node.setError(new GraphAbortedException(state.abortReason()));
        node.setStatus(NodeStatus.SKIPPED);
        emit(node, "node.skipped", NodeStatus.SKIPPED.displayName(),
                elapsed, node.startedAtNanos(), state.abortReason(), Map.of());
        finishClaimed(node, state);
    }

    /**
     * Finish a node that has already won {@link RunState#markCompleted}.
     * Completion is counted exactly once, then dependents are released and
     * the ready queue is drained.
     */
    private void finishClaimed(Node node, RunState state) {
        state.completeOne();
        state.releaseDependents(node);
        state.pump();
    }

    private void handleTimeout(Node node, RunState state, String key,
                               long elapsed, Duration timeout,
                               Throwable error) {
        TimeoutTracker.Entry entry = timeoutTracker.recordTimeout(key,
                state.policy);
        node.setError(error);

        // The per-node deadline may be the graph deadline itself. If the node
        // timer wins the race against the watchdog, still mark the run aborted.
        if (graphDeadlineReached(state)) {
            state.abort("graph timeout after " + state.policy.graphTimeout());
        }

        boolean recovered = safeFallback(node, state.ctx, error);
        Map<String, String> attrs = timeoutAttrs(timeout, entry);
        if (recovered) {
            node.setStatus(NodeStatus.FALLBACK);
            emit(node, "node.fallback", NodeStatus.FALLBACK.displayName(),
                    elapsed, node.startedAtNanos(), null, attrs);
        } else {
            node.setStatus(NodeStatus.TIMED_OUT);
            emit(node, "node.timeout", NodeStatus.TIMED_OUT.displayName(),
                    elapsed, node.startedAtNanos(), describeError(error), attrs);
        }

        if (entry.quarantined()) {
            emit(node, "node.quarantined", "quarantined",
                    elapsed, node.startedAtNanos(), null, attrs);
            if (state.policy.repeatedAction() == TimeoutAction.ABORT_GRAPH) {
                state.abort("node '" + node.id() + "' repeatedly timed out");
            }
        }
    }

    private void handleQuarantined(Node node, RunState state, String key,
                                   long start) {
        if (!state.markCompleted(node)) {
            return;
        }
        TimeoutTracker.Entry entry = timeoutTracker.snapshot(key);
        long elapsed = Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        DagTimeoutException error = new DagTimeoutException(
                node.id(), Duration.ZERO, true);
        node.setError(error);

        boolean recovered = safeFallback(node, state.ctx, error);
        node.setStatus(recovered ? NodeStatus.FALLBACK : NodeStatus.SKIPPED);
        Map<String, String> attrs = timeoutAttrs(Duration.ZERO, entry);
        emit(node, "node.quarantined",
                recovered ? NodeStatus.FALLBACK.displayName()
                        : NodeStatus.SKIPPED.displayName(),
                elapsed, node.startedAtNanos(), null, attrs);

        if (state.policy.repeatedAction() == TimeoutAction.ABORT_GRAPH) {
            state.abort("node '" + node.id() + "' is quarantined");
        }
        finishClaimed(node, state);
    }

    private static String timeoutKey(Node node) {
        return node.type().isEmpty() ? node.id() : node.type();
    }

    private Duration effectiveTimeout(Node node, TimeoutPolicy policy,
                                      RunState state) {
        Duration nodeTimeout = node.timeout();
        Duration base = nodeTimeout != null && !nodeTimeout.isZero()
                ? nodeTimeout : policy.defaultTimeout();

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
        Duration graphTimeout = state.policy.graphTimeout();
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

    private static boolean safeFallback(Node node, IContext ctx, Throwable error) {
        try {
            return node.fallback(ctx, error);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static Thread startGraphWatchdog(RunState state, TimeoutPolicy policy) {
        if (policy.graphTimeout().isZero()) {
            return null;
        }
        Duration timeout = policy.graphTimeout();
        return Thread.ofVirtual().name("lark-graph-timeout").start(() -> {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ignored) {
                return;
            }
            state.abort("graph timeout after " + timeout);
        });
    }

    private void installPoolsInto(IContext ctx) {
        ctx.installPool(PoolKind.COMPUTE, computePool);
        ctx.installPool(PoolKind.IO, ioPool);
        ctx.installPool(PoolKind.BACKGROUND, backgroundPool);
    }

    private void emit(Node node, String action, String status,
                      long elapsedNanos, long startedNanos, String error,
                      Map<String, String> attrs) {
        Monitor current = monitor;
        if (current == null || current == NullMonitor.INSTANCE) {
            return;
        }
        try {
            Event event = new Event("dag", action, node.id())
                    .attr("type", node.type())
                    .attr("status", status)
                    .attrNanos("started_ns", startedNanos);
            if (elapsedNanos >= 0) {
                event.attrNanos("elapsed_ns", elapsedNanos);
            }
            if (error != null && !error.isEmpty()) {
                event.attr("error", error);
            }
            for (Map.Entry<String, String> attr : attrs.entrySet()) {
                event.attr(attr.getKey(), attr.getValue());
            }
            event.durationNanos(Math.max(0, elapsedNanos))
                    .ok(error == null || error.isEmpty());
            current.emit(event);
        } catch (Throwable ignored) {
            // Monitoring must never break graph execution.
        }
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

    private static ThreadFactory platformFactory(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return runnable -> {
            Thread thread = new Thread(runnable,
                    prefix + sequence.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            computePool.shutdownNow();
            ioPool.shutdownNow();
            backgroundPool.shutdownNow();
            nodeBodyPool.shutdownNow();
            timeoutScheduler.shutdownNow();
        }
    }

    /** Shared coordination state for one execution. */
    private static final class RunState {

        private final Executor owner;
        private final IContext ctx;
        private final TimeoutPolicy policy;
        private final boolean[] disabled;

        private final GraphPlan plan;
        private final Node[] nodes;
        private final AtomicIntegerArray pending;
        private final int[][] dependentIndices;
        private final ConcurrentLinkedQueue<Node> ready =
                new ConcurrentLinkedQueue<>();
        private final AtomicBoolean pumping = new AtomicBoolean(false);
        private final AtomicIntegerArray completed;

        private final AtomicInteger remaining;
        private final CountDownLatch done = new CountDownLatch(1);
        private final Future<?>[] running;
        private final ScheduledFuture<?>[] timers;
        private final AtomicBoolean aborted = new AtomicBoolean(false);
        private volatile String abortReason = "";
        private long runStartNanos;

        private RunState(Graph graph, Collection<String> disabledIds,
                         Executor owner, IContext ctx, TimeoutPolicy policy) {
            this.owner = owner;
            this.ctx = ctx;
            this.policy = policy;
            this.plan = graph.plan();
            this.nodes = plan.nodes();
            int count = nodes.length;
            this.remaining = new AtomicInteger(count);
            this.pending = new AtomicIntegerArray(plan.initialPending());
            this.completed = new AtomicIntegerArray(count);
            this.running = new Future<?>[count];
            this.timers = new ScheduledFuture<?>[count];
            this.dependentIndices = plan.dependentIndices();
            this.disabled = new boolean[count];

            if (disabledIds != null) {
                for (String id : disabledIds) {
                    graph.find(id).ifPresent(node -> disabled[node.graphIndex()] = true);
                }
            }

            for (int root : plan.roots()) {
                ready.add(nodes[root]);
            }
        }

        private int indexOf(Node node) {
            int value = node.graphIndex();
            if (value < 0 || value >= nodes.length) {
                throw new IllegalStateException(
                        "Executor: dependency outside this graph: " + node.id());
            }
            return value;
        }

        private void start() {
            owner.pump(this);
        }

        private void pump() {
            owner.pump(this);
        }

        private boolean disabled(Node node) {
            return disabled[node.graphIndex()];
        }

        private boolean markCompleted(Node node) {
            return completed.compareAndSet(node.graphIndex(), 0, 1);
        }

        private void releaseDependents(Node node) {
            for (int dependentIndex : dependentIndices[indexOf(node)]) {
                if (pending.decrementAndGet(dependentIndex) == 0) {
                    ready.add(nodes[dependentIndex]);
                }
            }
        }

        private void registerRunning(Node node, Future<?> future) {
            int index = node.graphIndex();
            synchronized (running) {
                if (completed.get(index) == 1) {
                    return;
                }
                running[index] = future;
                if (aborted.get()) {
                    future.cancel(true);
                }
            }
        }

        private void unregisterRunning(Node node) {
            synchronized (running) {
                running[node.graphIndex()] = null;
            }
        }

        private void cancelRunning(Node node) {
            Future<?> future;
            synchronized (running) {
                future = running[node.graphIndex()];
            }
            if (future != null) {
                future.cancel(true);
            }
        }

        private void registerTimer(Node node, ScheduledFuture<?> timer) {
            int index = node.graphIndex();
            synchronized (timers) {
                if (completed.get(index) == 1) {
                    timer.cancel(false);
                    return;
                }
                timers[index] = timer;
            }
        }

        private void cancelTimer(Node node) {
            int index = node.graphIndex();
            synchronized (timers) {
                ScheduledFuture<?> timer = timers[index];
                if (timer != null) {
                    timers[index] = null;
                    timer.cancel(false);
                }
            }
        }

        private void abort(String reason) {
            if (!aborted.compareAndSet(false, true)) {
                return;
            }
            abortReason = reason;
            List<Future<?>> futures = new ArrayList<>();
            synchronized (running) {
                for (Future<?> future : running) {
                    if (future != null) {
                        futures.add(future);
                    }
                }
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
                done.countDown();
            }
        }

        private void await() {
            try {
                done.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "Interrupted while waiting for the DAG to complete", e);
            }
        }
    }
}
