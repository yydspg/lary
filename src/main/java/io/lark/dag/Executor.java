// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;
import io.lark.metric.NullMonitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Drives a {@link Graph} to completion with one virtual thread per node.
 *
 * <p>Model (the Java 21 analogue of the C++ coroutine executor): every node
 * gets a dedicated virtual thread. That thread blocks on its dependencies'
 * completion futures — cheaply parking the virtual thread — then runs the
 * node body on a virtual worker. Independent nodes therefore run concurrently,
 * and a wide graph never deadlocks on a small worker pool.
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
        RunState state = new RunState(graph.size());
        state.runStartNanos = System.nanoTime();
        Thread watchdog = startGraphWatchdog(state, policy);

        Set<String> disabled = disabledIds == null
                ? Set.of() : new HashSet<>(disabledIds);

        try {
            for (Node node : graph.nodes()) {
                if (disabled.contains(node.id())) {
                    skipNode(node, null, state, 0, "batch-disabled");
                    continue;
                }
                spawnNode(node, ctx, state, policy);
            }
            state.await();
        } finally {
            if (watchdog != null) {
                watchdog.interrupt();
            }
        }

        lastRunAborted = state.aborted();
        lastAbortReason = state.abortReason();
    }

    private void spawnNode(Node node, IContext ctx, RunState state,
                           TimeoutPolicy policy) {
        try {
            Thread.ofVirtual().name("lark-node-" + node.id()).start(() ->
                    driveNode(node, ctx, state, policy));
        } catch (Throwable fatal) {
            // Virtual thread creation refused (e.g. JVM shutdown): drain.
            node.setError(fatal);
            node.setStatus(NodeStatus.FAILED);
            emit(node, "node.failure", NodeStatus.FAILED.displayName(),
                    0, 0, describeError(fatal), Map.of());
            node.doneEvent().complete(null);
            state.completeOne();
        }
    }

    private void driveNode(Node node, IContext ctx, RunState state,
                           TimeoutPolicy policy) {
        try {
            for (Node dependency : node.dependencies()) {
                dependency.doneEvent().join();
            }
            if (state.aborted()) {
                skipNode(node, new GraphAbortedException(state.abortReason()),
                        state, 0, state.abortReason());
                return;
            }

            long start = System.nanoTime();
            node.setStartedAtNanos(start - state.runStartNanos);
            node.setStatus(NodeStatus.RUNNING);
            emit(node, "node.start", NodeStatus.RUNNING.displayName(),
                    -1, node.startedAtNanos(), null, Map.of());

            String key = timeoutKey(node);
            if (policy.quarantineEnabled() && timeoutTracker.isQuarantined(key)) {
                handleQuarantined(node, ctx, state, policy, key, start);
                return;
            }

            Duration effective = effectiveTimeout(node, policy, state);
            if (state.aborted()) {
                skipNode(node, new GraphAbortedException(state.abortReason()),
                        state, System.nanoTime() - start, state.abortReason());
                return;
            }

            Throwable error;
            boolean timedOut = false;
            Future<?> future = null;
            try {
                future = nodeBodyPool.submit(() -> {
                    node.run(ctx);
                    return null;
                });
                state.registerRunning(node, future);
                try {
                    if (effective.isZero()) {
                        future.get();
                    } else {
                        future.get(effective.toNanos(), TimeUnit.NANOSECONDS);
                    }
                    error = null;
                } catch (java.util.concurrent.TimeoutException timeout) {
                    timedOut = true;
                    future.cancel(policy.interruptOnTimeout());
                    error = new DagTimeoutException(node.id(), effective, false);
                } catch (CancellationException cancelled) {
                    if (state.aborted()) {
                        finishAborted(node, state, start);
                        return;
                    }
                    error = cancelled;
                } catch (ExecutionException execution) {
                    error = unwrap(execution.getCause());
                } finally {
                    state.unregisterRunning(node);
                }
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                error = interrupted;
            } catch (Throwable fatal) {
                error = unwrap(fatal);
            }

            long elapsed = Math.max(0, System.nanoTime() - start);
            node.setElapsedNanos(elapsed);

            if (timedOut) {
                handleTimeout(node, ctx, state, policy, key, start, elapsed,
                        effective, error);
            } else {
                timeoutTracker.recordSuccess(key);
                if (error == null) {
                    node.setStatus(NodeStatus.SUCCESS);
                    emit(node, "node.success", NodeStatus.SUCCESS.displayName(),
                            elapsed, node.startedAtNanos(), null, Map.of());
                } else {
                    node.setError(error);
                    boolean recovered = safeFallback(node, ctx, error);
                    if (recovered) {
                        node.setStatus(NodeStatus.FALLBACK);
                        emit(node, "node.fallback",
                                NodeStatus.FALLBACK.displayName(),
                                elapsed, node.startedAtNanos(), null, Map.of());
                    } else {
                        node.setStatus(NodeStatus.FAILED);
                        emit(node, "node.failure",
                                NodeStatus.FAILED.displayName(),
                                elapsed, node.startedAtNanos(),
                                describeError(error), Map.of());
                    }
                }
            }
        } catch (Throwable fatal) {
            // Defensive: never leave the graph undrained.
            node.setError(unwrap(fatal));
            node.setStatus(NodeStatus.FAILED);
            emit(node, "node.failure", NodeStatus.FAILED.displayName(),
                    node.elapsedNanos(), node.startedAtNanos(),
                    describeError(fatal), Map.of());
        } finally {
            // Always unblock dependents, even on failure/timeout.
            node.doneEvent().complete(null);
            state.completeOne();
        }
    }

    private void handleTimeout(Node node, IContext ctx, RunState state,
                               TimeoutPolicy policy, String key, long start,
                               long elapsed, Duration timeout,
                               Throwable error) {
        TimeoutTracker.Entry entry = timeoutTracker.recordTimeout(key, policy);
        node.setError(error);

        boolean recovered = safeFallback(node, ctx, error);
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
            if (policy.repeatedAction() == TimeoutAction.ABORT_GRAPH) {
                state.abort("node '" + node.id() + "' repeatedly timed out");
            }
        }
    }

    private void handleQuarantined(Node node, IContext ctx, RunState state,
                                   TimeoutPolicy policy, String key,
                                   long start) {
        TimeoutTracker.Entry entry = timeoutTracker.snapshot(key);
        long elapsed = Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        DagTimeoutException error = new DagTimeoutException(
                node.id(), Duration.ZERO, true);
        node.setError(error);

        boolean recovered = safeFallback(node, ctx, error);
        node.setStatus(recovered ? NodeStatus.FALLBACK : NodeStatus.SKIPPED);
        Map<String, String> attrs = timeoutAttrs(Duration.ZERO, entry);
        emit(node, "node.quarantined",
                recovered ? NodeStatus.FALLBACK.displayName()
                        : NodeStatus.SKIPPED.displayName(),
                elapsed, node.startedAtNanos(), null, attrs);

        if (policy.repeatedAction() == TimeoutAction.ABORT_GRAPH) {
            state.abort("node '" + node.id() + "' is quarantined");
        }
    }

    private void finishAborted(Node node, RunState state, long start) {
        long elapsed = Math.max(0, System.nanoTime() - start);
        node.setElapsedNanos(elapsed);
        node.setError(new GraphAbortedException(state.abortReason()));
        node.setStatus(NodeStatus.SKIPPED);
        emit(node, "node.skipped", NodeStatus.SKIPPED.displayName(),
                elapsed, node.startedAtNanos(), state.abortReason(), Map.of());
    }

    private void skipNode(Node node, Throwable error, RunState state,
                          long elapsedNanos, String reason) {
        node.setStatus(NodeStatus.SKIPPED);
        node.setError(error);
        node.setElapsedNanos(Math.max(0, elapsedNanos));
        emit(node, "node.skipped", NodeStatus.SKIPPED.displayName(),
                node.elapsedNanos(), node.startedAtNanos(), reason, Map.of());
        node.doneEvent().complete(null);
        state.completeOne();
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
        }
    }

    /** Shared coordination state for one execution. */
    private static final class RunState {

        private final AtomicInteger remaining;
        private final CountDownLatch done = new CountDownLatch(1);
        private final AtomicBoolean aborted = new AtomicBoolean(false);
        private final Map<Node, Future<?>> running = new HashMap<>();
        private volatile String abortReason = "";
        private long runStartNanos;

        private RunState(int nodeCount) {
            this.remaining = new AtomicInteger(nodeCount);
        }

        private void registerRunning(Node node, Future<?> future) {
            synchronized (running) {
                running.put(node, future);
                if (aborted.get()) {
                    future.cancel(true);
                }
            }
        }

        private void unregisterRunning(Node node) {
            synchronized (running) {
                running.remove(node);
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
