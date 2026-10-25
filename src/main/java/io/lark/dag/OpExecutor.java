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
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Executes an {@link OpGraph} on a dedicated pool.
 *
 * <p>Each op gets a virtual driver thread that waits for dependency futures,
 * then runs the op body on the supplied pool. Timeout support mirrors the Node
 * executor: per-op deadlines, business {@link Op#fallback}, repeated-timeout
 * detection ({@link TimeoutTracker}), quarantine and optional graph abort.
 *
 * <p>A failed op does not poison dependents: its completion future still
 * resolves so downstream ops run and can inspect the executor status.
 */
public final class OpExecutor implements AutoCloseable {

    private final ExecutorService pool;
    private final boolean ownsPool;
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
        RunState state = new RunState(order.size(), policy);
        state.runStartNanos = System.nanoTime();
        Thread watchdog = startGraphWatchdog(state, policy);

        Map<Op, CompletableFuture<Void>> done = new IdentityHashMap<>();
        for (Op op : order) {
            done.put(op, new CompletableFuture<>());
        }
        for (Op op : order) {
            Thread.ofVirtual().name("lark-op-driver-" + op.name()).start(() ->
                    driveOp(graph, op, data, done, state));
        }

        CompletableFuture<Void> all = state.allDone();
        all.whenComplete((ignored, error) -> {
            if (watchdog != null) {
                watchdog.interrupt();
            }
            lastRunAborted = state.aborted();
            lastAbortReason = state.abortReason();
        });
        return all;
    }

    private void driveOp(OpGraph graph, Op op, IContext data,
                         Map<Op, CompletableFuture<Void>> done,
                         RunState state) {
        CompletableFuture<Void> opDone = done.get(op);
        try {
            for (Op dependency : graph.depsOf(op)) {
                done.get(dependency).join();
            }
            if (state.aborted()) {
                finishSkipped(op, opDone, state, state.abortReason());
                return;
            }

            setStatus(op, OpStatus.RUNNING);
            emit("op.start", op.name(), true, 0, Map.of());

            try {
                for (OpAspect aspect : graph.aspects()) {
                    if (aspect.shouldSkip(op, data)) {
                        setStatus(op, OpStatus.SKIPPED);
                        emit("op.skipped", op.name(), true, 0, Map.of());
                        opDone.complete(null);
                        state.completeOne();
                        return;
                    }
                }
                for (OpAspect aspect : graph.aspects()) {
                    aspect.onBefore(op, data);
                }
            } catch (Throwable error) {
                setStatus(op, OpStatus.FAILED);
                emit("op.error", op.name(), false, 0,
                        Map.of("error", describeError(error)));
                opDone.complete(null);
                state.completeOne();
                return;
            }

            String key = op.name();
            if (state.policy().quarantineEnabled()
                    && timeoutTracker.isQuarantined(key)) {
                handleQuarantined(graph, op, data, opDone, state, key);
                return;
            }

            Duration effective = effectiveTimeout(op, state.policy(), state);
            if (state.aborted()) {
                finishSkipped(op, opDone, state, state.abortReason());
                return;
            }

            long start = System.nanoTime();
            Throwable error;
            boolean timedOut = false;
            Future<?> future = null;
            try {
                future = pool.submit(() -> {
                    op.execute(data);
                    return null;
                });
                state.registerRunning(op, future);
                try {
                    if (effective.isZero()) {
                        future.get();
                    } else {
                        future.get(effective.toNanos(), TimeUnit.NANOSECONDS);
                    }
                    error = null;
                } catch (java.util.concurrent.TimeoutException timeout) {
                    timedOut = true;
                    future.cancel(state.policy().interruptOnTimeout());
                    error = new DagTimeoutException(op.name(), effective, false);
                } catch (CancellationException cancelled) {
                    if (state.aborted()) {
                        finishSkipped(op, opDone, state, state.abortReason());
                        return;
                    }
                    error = cancelled;
                } catch (ExecutionException execution) {
                    error = unwrap(execution.getCause());
                } finally {
                    state.unregisterRunning(op);
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                error = interrupted;
            } catch (Throwable fatal) {
                error = unwrap(fatal);
            }

            long elapsed = Math.max(0, System.nanoTime() - start);
            if (timedOut) {
                handleTimeout(graph, op, data, opDone, state, key,
                        elapsed, effective, error);
            } else {
                timeoutTracker.recordSuccess(key);
                handleCompletion(graph, op, data, opDone, state,
                        error, elapsed);
            }
        } catch (Throwable fatal) {
            setStatus(op, OpStatus.FAILED);
            opDone.complete(null);
            state.completeOne();
        }
    }

    private void handleTimeout(OpGraph graph, Op op, IContext data,
                               CompletableFuture<Void> opDone, RunState state,
                               String key, long elapsed, Duration timeout,
                               Throwable error) {
        TimeoutTracker.Entry entry = timeoutTracker.recordTimeout(key,
                state.policy());
        Map<String, String> attrs = timeoutAttrs(timeout, entry);

        boolean recovered = safeFallback(op, data, error);
        OpStatus outcome = recovered ? OpStatus.FALLBACK : OpStatus.TIMED_OUT;
        setStatus(op, outcome);
        if (recovered) {
            emit("op.fallback", op.name(), true, elapsed, attrs);
        } else {
            emit("op.timeout", op.name(), false, elapsed, attrs);
        }
        notifyAfter(graph, op, data, outcome);

        if (entry.quarantined()) {
            emit("op.quarantined", op.name(), true, elapsed, attrs);
            if (state.policy().repeatedAction() == TimeoutAction.ABORT_GRAPH) {
                state.abort("op '" + op.name() + "' repeatedly timed out");
            }
        }

        opDone.complete(null);
        state.completeOne();
    }

    private void handleQuarantined(OpGraph graph, Op op, IContext data,
                                   CompletableFuture<Void> opDone,
                                   RunState state, String key) {
        TimeoutTracker.Entry entry = timeoutTracker.snapshot(key);
        DagTimeoutException error = new DagTimeoutException(
                op.name(), Duration.ZERO, true);
        boolean recovered = safeFallback(op, data, error);
        OpStatus outcome = recovered ? OpStatus.FALLBACK : OpStatus.SKIPPED;
        setStatus(op, outcome);
        Map<String, String> attrs = timeoutAttrs(Duration.ZERO, entry);
        emit("op.quarantined", op.name(),
                recovered, 0, attrs);
        notifyAfter(graph, op, data, outcome);

        if (state.policy().repeatedAction() == TimeoutAction.ABORT_GRAPH) {
            state.abort("op '" + op.name() + "' is quarantined");
        }
        opDone.complete(null);
        state.completeOne();
    }

    private void handleCompletion(OpGraph graph, Op op, IContext data,
                                  CompletableFuture<Void> opDone,
                                  RunState state, Throwable error,
                                  long elapsed) {
        OpStatus outcome;
        if (error == null) {
            outcome = OpStatus.SUCCESS;
            emit("op.end", op.name(), true, elapsed, Map.of());
        } else {
            boolean recovered = safeFallback(op, data, error);
            outcome = recovered ? OpStatus.FALLBACK : OpStatus.FAILED;
            if (recovered) {
                emit("op.fallback", op.name(), true, elapsed, Map.of());
            } else {
                emit("op.error", op.name(), false, elapsed,
                        Map.of("error", describeError(error)));
            }
        }
        setStatus(op, outcome);
        notifyAfter(graph, op, data, outcome);
        opDone.complete(null);
        state.completeOne();
    }

    private void finishSkipped(Op op, CompletableFuture<Void> opDone,
                               RunState state, String reason) {
        setStatus(op, OpStatus.SKIPPED);
        emit("op.skipped", op.name(), true, 0,
                Map.of("reason", reason == null ? "" : reason));
        opDone.complete(null);
        state.completeOne();
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
                      long elapsedNanos, Map<String, String> attrs) {
        Monitor current = monitor;
        if (current == null || current == NullMonitor.INSTANCE) {
            return;
        }
        try {
            Event event = new Event("dag", action, name)
                    .durationNanos(Math.max(0, elapsedNanos))
                    .ok(ok);
            for (Map.Entry<String, String> attr : attrs.entrySet()) {
                event.attr(attr.getKey(), attr.getValue());
            }
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

    @Override
    public void close() {
        if (closed.compareAndSet(false, true) && ownsPool) {
            pool.shutdownNow();
        }
    }

    private static ExecutorService createVirtualPool() {
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("lark-op-", 0).factory());
    }

    private static final class RunState {
        private final AtomicInteger remaining;
        private final CompletableFuture<Void> allDone = new CompletableFuture<>();
        private final AtomicBoolean aborted = new AtomicBoolean();
        private final Map<Op, Future<?>> running = new IdentityHashMap<>();
        private final TimeoutPolicy policy;
        private volatile String abortReason = "";
        private long runStartNanos;

        private RunState(int opCount, TimeoutPolicy policy) {
            this.remaining = new AtomicInteger(opCount);
            this.policy = policy;
        }

        private TimeoutPolicy policy() {
            return policy;
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
                allDone.complete(null);
            }
        }

        private CompletableFuture<Void> allDone() {
            return allDone;
        }
    }
}
