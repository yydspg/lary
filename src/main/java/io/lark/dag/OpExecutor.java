// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;
import io.lark.metric.NullMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Executes an {@link OpGraph} asynchronously on a dedicated pool.
 *
 * <p>Every op waits for its dependency futures, then runs on the pool. With
 * the default constructor the pool is a virtual-thread-per-task executor, so
 * blocking business code is fine and a wide graph cannot starve the pool.
 * A custom {@link ExecutorService} may be supplied; it should be
 * blocking-friendly (virtual threads or a sufficiently large pool).
 *
 * <p>A failed op does not poison dependents: it is recorded as
 * {@link OpStatus#FAILED}, but the completion future still resolves so
 * downstream ops run and can inspect the executor status.
 */
public final class OpExecutor implements AutoCloseable {

    private final ExecutorService pool;
    private final boolean ownsPool;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Map<Op, OpStatus> statuses =
            Collections.synchronizedMap(new IdentityHashMap<>());

    private volatile Monitor monitor;

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

        Map<Op, CompletableFuture<Void>> futures = new IdentityHashMap<>();
        for (Op op : order) {
            List<CompletableFuture<Void>> depFutures = new ArrayList<>();
            for (Op dep : graph.depsOf(op)) {
                depFutures.add(futures.get(dep));
            }
            CompletableFuture<Void> ready = depFutures.isEmpty()
                    ? CompletableFuture.completedFuture(null)
                    : CompletableFuture.allOf(depFutures.toArray(CompletableFuture[]::new));

            futures.put(op, ready.thenRunAsync(
                    () -> runOp(graph, op, data), pool));
        }

        CompletableFuture<?>[] all = order.stream()
                .map(futures::get)
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(all);
    }

    /** Per-op status of the last execution. */
    public OpStatus status(Op op) {
        return statuses.getOrDefault(op, OpStatus.PENDING);
    }

    public boolean allSucceeded() {
        synchronized (statuses) {
            for (OpStatus status : statuses.values()) {
                if (status != OpStatus.SUCCESS && status != OpStatus.SKIPPED) {
                    return false;
                }
            }
            return true;
        }
    }

    private void runOp(OpGraph graph, Op op, IContext data) {
        setStatus(op, OpStatus.RUNNING);
        emit("op.start", op.name(), true, 0);
        long start = System.nanoTime();

        try {
            for (OpAspect aspect : graph.aspects()) {
                if (aspect.shouldSkip(op, data)) {
                    setStatus(op, OpStatus.SKIPPED);
                    emit("op.skipped", op.name(), true,
                            System.nanoTime() - start);
                    return;
                }
            }
            for (OpAspect aspect : graph.aspects()) {
                aspect.onBefore(op, data);
            }
        } catch (Throwable error) {
            setStatus(op, OpStatus.FAILED);
            emit("op.error", op.name(), false, System.nanoTime() - start);
            return;
        }

        OpStatus outcome = OpStatus.SUCCESS;
        try {
            op.execute(data);
        } catch (Throwable error) {
            outcome = OpStatus.FAILED;
        }
        setStatus(op, outcome);

        try {
            for (OpAspect aspect : graph.aspects()) {
                aspect.onAfter(op, data, outcome);
            }
        } catch (Throwable aspectError) {
            outcome = OpStatus.FAILED;
            setStatus(op, outcome);
        }

        long elapsed = System.nanoTime() - start;
        if (outcome == OpStatus.FAILED) {
            emit("op.error", op.name(), false, elapsed);
        } else {
            emit("op.end", op.name(), true, elapsed);
        }
    }

    private void setStatus(Op op, OpStatus status) {
        statuses.put(op, status);
    }

    private void emit(String action, String name, boolean ok, long elapsedNanos) {
        Monitor current = monitor;
        if (current == null || current == NullMonitor.INSTANCE) {
            return;
        }
        try {
            current.emit(new Event("dag", action, name)
                    .durationNanos(Math.max(0, elapsedNanos))
                    .ok(ok));
        } catch (Throwable ignored) {
            // Monitoring must never break graph execution.
        }
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
}
