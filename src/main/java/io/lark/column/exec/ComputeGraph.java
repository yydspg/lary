// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import io.lark.column.TensorStore;
import io.lark.column.op.FallibleOpTensor;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpTensor;
import io.lark.column.op.TensorOps;
import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Execution-layer DAG of {@link ComputeNode}s.
 *
 * <p>{@link #execute(ExecutionContext)} runs sequentially in topological
 * order. {@link #executeAsync(ExecutorService, ExecutionContext)} gives every
 * node its own virtual thread (the Java coroutine equivalent) and executes op
 * bodies on the supplied pool. Independent nodes run concurrently.
 *
 * <p>Placeholders support forward references during total-graph compilation;
 * they are replaced via {@link #replacePlaceholder} before execution.
 */
public final class ComputeGraph {

    private final List<ComputeNode> nodes = new ArrayList<>();
    private final Map<String, ComputeNode> byId = new LinkedHashMap<>();
    private List<ComputeNode> topo = List.of();
    private boolean finalized;

    public ComputeNode addNode(String id, String module, OpTensor op) {
        Objects.requireNonNull(id, "id");
        if (byId.containsKey(id)) {
            throw new IllegalArgumentException(
                    "ComputeGraph: duplicate node id '" + id + "'");
        }
        ComputeNode node = new ComputeNode(id, module, op);
        byId.put(id, node);
        nodes.add(node);
        finalized = false;
        return node;
    }

    public ComputeNode addPlaceholder(String output, String id) {
        return addNode(id, "@placeholder", TensorOps.placeholder(output));
    }

    public void replacePlaceholder(String placeholderId, String realId) {
        ComputeNode placeholder = byId.get(placeholderId);
        ComputeNode real = byId.get(realId);
        if (placeholder == null) {
            throw new NoSuchElementException(
                    "ComputeGraph.replacePlaceholder: unknown placeholder '"
                            + placeholderId + "'");
        }
        if (real == null) {
            throw new NoSuchElementException(
                    "ComputeGraph.replacePlaceholder: unknown node '" + realId + "'");
        }
        if (placeholder == real) {
            return;
        }
        for (ComputeNode node : nodes) {
            node.replaceDependency(placeholder, real);
        }
        byId.remove(placeholderId);
        nodes.remove(placeholder);
        finalized = false;
    }

    public boolean hasPlaceholders() {
        for (ComputeNode node : nodes) {
            if ("placeholder".equals(node.op().opType())) {
                return true;
            }
        }
        return false;
    }

    public Optional<ComputeNode> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<ComputeNode> nodes() {
        return List.copyOf(nodes);
    }

    public int size() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public boolean finalized() {
        return finalized;
    }

    /** Wire a dependency edge (both ids must exist); idempotent. */
    public void addDependency(String id, String dependencyId) {
        ComputeNode node = byId.get(id);
        ComputeNode dependency = byId.get(dependencyId);
        if (node == null) {
            throw new NoSuchElementException(
                    "ComputeGraph.addDependency: unknown node '" + id + "'");
        }
        if (dependency == null) {
            throw new NoSuchElementException(
                    "ComputeGraph.addDependency: unknown dep '" + dependencyId + "'");
        }
        if (!node.dependencies().contains(dependency)) {
            node.addDependency(dependency);
        }
        finalized = false;
    }

    /** Topologically sort and detect cycles (Kahn's algorithm). */
    public void finalizeGraph() {
        Map<ComputeNode, Integer> inDegree = new IdentityHashMap<>();
        Map<ComputeNode, List<ComputeNode>> outgoing = new IdentityHashMap<>();

        for (ComputeNode node : nodes) {
            inDegree.put(node, 0);
        }
        for (ComputeNode node : nodes) {
            for (ComputeNode dependency : node.dependencies()) {
                outgoing.computeIfAbsent(dependency, key -> new ArrayList<>()).add(node);
                inDegree.merge(node, 1, Integer::sum);
            }
        }

        Deque<ComputeNode> ready = new ArrayDeque<>();
        for (ComputeNode node : nodes) {
            if (inDegree.get(node) == 0) {
                ready.add(node);
            }
        }

        List<ComputeNode> order = new ArrayList<>(nodes.size());
        while (!ready.isEmpty()) {
            ComputeNode node = ready.poll();
            order.add(node);
            for (ComputeNode next : outgoing.getOrDefault(node, List.of())) {
                int degree = inDegree.merge(next, -1, Integer::sum);
                if (degree == 0) {
                    ready.add(next);
                }
            }
        }

        if (order.size() != nodes.size()) {
            throw new IllegalStateException("ComputeGraph: dependency cycle detected");
        }

        topo = List.copyOf(order);
        finalized = true;
    }

    /** Sequential execution in topological order on the calling thread. */
    public void execute(ExecutionContext ctx) {
        Objects.requireNonNull(ctx, "ctx");
        if (!finalized) {
            finalizeGraph();
        }
        resetRunState();
        if (nodes.isEmpty()) {
            return;
        }
        reserveOutputs(ctx.store());

        RunState state = new RunState(nodes.size());
        state.execStartNanos = System.nanoTime();

        for (ComputeNode node : topo) {
            long start = System.nanoTime();
            emitNode(ctx, "node.start", node, 0, true);
            ModuleAcc acc = state.module(node.module());
            synchronized (acc) {
                acc.count++;
                if (!acc.started) {
                    acc.started = true;
                    acc.startNanos = start;
                }
            }

            Throwable error = null;
            try {
                node.op().compute(new OpContext(ctx.store()));
            } catch (Throwable t) {
                error = t;
            }

            long elapsed = System.nanoTime() - start;
            node.setElapsedNanos(elapsed);
            state.busyNanos.addAndGet(elapsed);
            synchronized (acc) {
                acc.endNanos = Math.max(acc.endNanos, start + elapsed);
            }
            boolean recovered = error != null
                    && invokeFallback(node, ctx, error);
            emitNode(ctx, error == null ? "node.end"
                            : recovered ? "node.fallback" : "node.error",
                    node, elapsed, error == null || recovered);
            node.setStatus(error == null ? ComputeNode.Status.SUCCESS
                    : recovered ? ComputeNode.Status.FALLBACK
                    : ComputeNode.Status.FAILED);
            if (error != null) {
                node.setError(error);
            }
            node.doneEvent().complete(null);
        }

        long wall = System.nanoTime() - state.execStartNanos;
        reportRunSummary(ctx, state, wall, 1);
    }

    /**
     * Asynchronous execution: one virtual thread per node; op bodies run on
     * {@code pool}. Blocks until every node completed.
     */
    public void executeAsync(ExecutorService pool, ExecutionContext ctx) {
        executeAsync(pool, ctx, workers(pool));
    }

    /**
     * Asynchronous execution with an explicit worker-count hint for the
     * CPU-pressure metric.
     */
    public void executeAsync(ExecutorService pool, ExecutionContext ctx,
                             int workersHint) {
        Objects.requireNonNull(pool, "pool");
        Objects.requireNonNull(ctx, "ctx");
        if (!finalized) {
            finalizeGraph();
        }
        resetRunState();
        if (nodes.isEmpty()) {
            return;
        }
        reserveOutputs(ctx.store());

        int workers = Math.max(1, workersHint);
        RunState state = new RunState(nodes.size());
        state.execStartNanos = System.nanoTime();

        for (ComputeNode node : nodes) {
            try {
                Thread.ofVirtual().name("lark-column-" + node.id()).start(() ->
                        runNodeAsync(node, pool, ctx, state));
            } catch (Throwable t) {
                node.setStatus(ComputeNode.Status.FAILED);
                node.setError(t);
                node.doneEvent().complete(null);
                state.completeOne();
            }
        }

        state.await();
        long wall = System.nanoTime() - state.execStartNanos;
        reportRunSummary(ctx, state, wall, workers);
    }

    private void runNodeAsync(ComputeNode node, ExecutorService pool,
                              ExecutionContext ctx, RunState state) {
        try {
            for (ComputeNode dependency : node.dependencies()) {
                dependency.doneEvent().join();
            }

            long start = System.nanoTime();
            emitNode(ctx, "node.start", node, 0, true);
            ModuleAcc acc = state.module(node.module());
            synchronized (acc) {
                acc.count++;
                if (!acc.started) {
                    acc.started = true;
                    acc.startNanos = start;
                }
            }

            Throwable error = null;
            try {
                pool.submit(() -> {
                    node.op().compute(new OpContext(ctx.store()));
                    return null;
                }).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                error = e;
            } catch (ExecutionException e) {
                error = unwrap(e.getCause());
            } catch (Throwable t) {
                error = unwrap(t);
            }

            long elapsed = System.nanoTime() - start;
            node.setElapsedNanos(elapsed);
            state.busyNanos.addAndGet(elapsed);
            synchronized (acc) {
                acc.endNanos = Math.max(acc.endNanos, start + elapsed);
            }
            boolean recovered = error != null
                    && invokeFallback(node, ctx, error);
            emitNode(ctx, error == null ? "node.end"
                            : recovered ? "node.fallback" : "node.error",
                    node, elapsed, error == null || recovered);
            node.setStatus(error == null ? ComputeNode.Status.SUCCESS
                    : recovered ? ComputeNode.Status.FALLBACK
                    : ComputeNode.Status.FAILED);
            if (error != null) {
                node.setError(error);
            }
        } catch (Throwable t) {
            node.setStatus(ComputeNode.Status.FAILED);
            node.setError(unwrap(t));
        } finally {
            node.doneEvent().complete(null);
            state.completeOne();
        }
    }

    private void resetRunState() {
        for (ComputeNode node : nodes) {
            node.resetRunState();
        }
    }

    private void reserveOutputs(TensorStore store) {
        for (ComputeNode node : nodes) {
            store.ensureNames(node.op().outputs());
        }
    }

    private static int workers(ExecutorService pool) {
        return pool instanceof ThreadPoolExecutor executor
                ? Math.max(1, executor.getMaximumPoolSize())
                : Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    private void emitNode(ExecutionContext ctx, String action, ComputeNode node,
                          long elapsedNanos, boolean ok) {
        if (!ctx.hasMonitor()) {
            return;
        }
        Monitor monitor = ctx.monitor();
        try {
            monitor.emit(new Event("column", action, node.id())
                    .attr("op", node.op().opType())
                    .attrNanos("elapsed_ns", elapsedNanos)
                    .durationNanos(Math.max(0, elapsedNanos))
                    .ok(ok));
        } catch (Throwable ignored) {
            // Monitoring must never break computation.
        }
    }

    private void reportRunSummary(ExecutionContext ctx, RunState state,
                                  long wallNanos, int workers) {
        if (!ctx.hasMonitor()) {
            return;
        }
        Monitor monitor = ctx.monitor();
        try {
            for (Map.Entry<String, ModuleAcc> entry : state.modules.entrySet()) {
                ModuleAcc acc = entry.getValue();
                synchronized (acc) {
                    long duration = acc.started ? Math.max(0, acc.endNanos - acc.startNanos) : 0;
                    monitor.emit(new Event("column", "module.end", entry.getKey())
                            .durationNanos(duration)
                            .attr("node_count", Integer.toString(acc.count)));
                }
            }

            double utilization = 0.0;
            long busy = state.busyNanos.get();
            if (wallNanos > 0 && workers > 0) {
                utilization = Math.min(1.0,
                        (double) busy / ((double) wallNanos * workers));
            }
            monitor.emit(new Event("column", "cpu.pressure", "compute")
                    .attr("pool", "compute")
                    .attr("utilization", Double.toString(utilization))
                    .attrNanos("busy_ns", busy)
                    .attrNanos("wall_ns", wallNanos)
                    .attr("workers", Integer.toString(workers)));
        } catch (Throwable ignored) {
            // Monitoring must never break computation.
        }
    }

    private static boolean invokeFallback(ComputeNode node, ExecutionContext ctx,
                                          Throwable error) {
        if (node.op() instanceof FallibleOpTensor fallible) {
            try {
                return fallible.fallback(new OpContext(ctx.store()), error);
            } catch (Throwable ignored) {
                return false;
            }
        }
        return false;
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

    private static final class ModuleAcc {
        private long startNanos;
        private long endNanos;
        private int count;
        private boolean started;
    }

    private static final class RunState {
        private final AtomicInteger remaining;
        private final CountDownLatch done = new CountDownLatch(1);
        private final Map<String, ModuleAcc> modules = new LinkedHashMap<>();
        private final AtomicLong busyNanos = new AtomicLong();
        private long execStartNanos;

        private RunState(int nodeCount) {
            this.remaining = new AtomicInteger(nodeCount);
        }

        private ModuleAcc module(String name) {
            synchronized (modules) {
                return modules.computeIfAbsent(name, key -> new ModuleAcc());
            }
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
                        "Interrupted while waiting for the compute graph", e);
            }
        }
    }
}
