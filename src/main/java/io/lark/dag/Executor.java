// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;
import io.lark.metric.NullMonitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Drives a {@link Graph} to completion with one virtual thread per node.
 *
 * <p>Model (the Java 21 analogue of the C++ coroutine executor): every node
 * gets a dedicated virtual thread. That thread blocks on its dependencies'
 * completion futures — cheaply parking the virtual thread — then runs the
 * node body. Independent nodes therefore run concurrently, and a wide graph
 * never deadlocks on a small worker pool.
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
 * <p>The compute pool is deliberately fixed so CPU-heavy work cannot oversubscribe
 * the machine. IO/background use virtual threads so blocking nested calls do
 * not starve a small platform pool.
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

    private final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile Monitor monitor = NullMonitor.INSTANCE;

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
    }

    /** Optional observability hook; any thread-safe {@link Monitor}. */
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor == null ? NullMonitor.INSTANCE : monitor;
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
        if (graph.isEmpty()) {
            return;
        }

        installPoolsInto(ctx);

        RunState state = new RunState(graph.size());
        long runStartNanos = System.nanoTime();
        Set<String> disabled = disabledIds == null
                ? Set.of() : new HashSet<>(disabledIds);

        for (Node node : graph.nodes()) {
            if (disabled.contains(node.id())) {
                node.setStatus(NodeStatus.SKIPPED);
                emit(node, "node.skipped", NodeStatus.SKIPPED.displayName(),
                        0, 0, null);
                node.doneEvent().complete(null);
                state.completeOne();
                continue;
            }
            spawnNode(node, ctx, state, runStartNanos);
        }

        state.await();
    }

    private void spawnNode(Node node, IContext ctx, RunState state,
                           long runStartNanos) {
        try {
            Thread.ofVirtual().name("lark-node-" + node.id()).start(() -> {
                try {
                    // Every dependency was spawned already; blocking here parks
                    // this virtual thread, not a platform worker.
                    for (Node dependency : node.dependencies()) {
                        dependency.doneEvent().join();
                    }

                    long start = System.nanoTime();
                    node.setStartedAtNanos(start - runStartNanos);
                    node.setStatus(NodeStatus.RUNNING);
                    emit(node, "node.start", NodeStatus.RUNNING.displayName(),
                            -1, node.startedAtNanos(), null);

                    Throwable error = null;
                    try {
                        node.run(ctx);
                    } catch (Throwable t) {
                        error = unwrap(t);
                    }

                    long elapsed = System.nanoTime() - start;
                    node.setElapsedNanos(elapsed);

                    if (error == null) {
                        node.setStatus(NodeStatus.SUCCESS);
                        emit(node, "node.success", NodeStatus.SUCCESS.displayName(),
                                elapsed, node.startedAtNanos(), null);
                    } else {
                        node.setError(error);
                        boolean recovered;
                        try {
                            recovered = node.fallback(ctx, error);
                        } catch (Throwable fallbackError) {
                            recovered = false;
                        }

                        if (recovered) {
                            node.setStatus(NodeStatus.FALLBACK);
                            emit(node, "node.fallback",
                                    NodeStatus.FALLBACK.displayName(),
                                    elapsed, node.startedAtNanos(), null);
                        } else {
                            node.setStatus(NodeStatus.FAILED);
                            emit(node, "node.failure",
                                    NodeStatus.FAILED.displayName(),
                                    elapsed, node.startedAtNanos(),
                                    describeError(error));
                        }
                    }
                } catch (Throwable fatal) {
                    // Defensive: never leave the graph undrained.
                    node.setError(unwrap(fatal));
                    node.setStatus(NodeStatus.FAILED);
                    emit(node, "node.failure", NodeStatus.FAILED.displayName(),
                            node.elapsedNanos(), node.startedAtNanos(),
                            describeError(fatal));
                } finally {
                    // Always unblock dependents, even on failure.
                    node.doneEvent().complete(null);
                    state.completeOne();
                }
            });
        } catch (Throwable fatal) {
            // Virtual thread creation refused (e.g. JVM shutdown): drain.
            node.setError(fatal);
            node.setStatus(NodeStatus.FAILED);
            node.doneEvent().complete(null);
            state.completeOne();
        }
    }

    private void installPoolsInto(IContext ctx) {
        ctx.installPool(PoolKind.COMPUTE, computePool);
        ctx.installPool(PoolKind.IO, ioPool);
        ctx.installPool(PoolKind.BACKGROUND, backgroundPool);
    }

    private void emit(Node node, String action, String status,
                      long elapsedNanos, long startedNanos, String error) {
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
        }
    }

    /** Shared coordination state for one execution. */
    private static final class RunState {

        private final AtomicInteger remaining;
        private final CountDownLatch done = new CountDownLatch(1);

        private RunState(int nodeCount) {
            this.remaining = new AtomicInteger(nodeCount);
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
