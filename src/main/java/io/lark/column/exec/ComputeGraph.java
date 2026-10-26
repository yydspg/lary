// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import io.lark.column.tensor.TensorStore;
import io.lark.column.tensor.TensorKernel;
import io.lark.column.tensor.TensorKernels;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;
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
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Execution-layer DAG of {@link ComputeNode}s.
 *
 * <p>Each node binds an {@link OpSpec} graph description to a
 * {@link TensorKernel} execution operator. {@link #execute(ExecutionContext)}
 * runs sequentially in topological order.
 * {@link #executeAsync(ExecutorService, ExecutionContext)} uses a
 * dependency-counter ready queue: a node is submitted to the supplied pool only
 * after all of its dependencies completed, and its op body runs directly on a
 * pool thread. Independent nodes run concurrently and no thread is parked while
 * waiting for dependencies.
 *
 * <p>Placeholders support forward references during total-graph compilation;
 * they are replaced via {@link #replacePlaceholder} before execution.
 */
public final class ComputeGraph {

    private final List<ComputeNode> nodes = new ArrayList<>();
    private final Map<String, ComputeNode> byId = new LinkedHashMap<>();
    private List<ComputeNode> topo = List.of();
    private boolean finalized;

    public ComputeNode addNode(String id, String module, OpSpec op,
                               TensorKernel kernel) {
        Objects.requireNonNull(id, "id");
        if (byId.containsKey(id)) {
            throw new IllegalArgumentException(
                    "ComputeGraph: duplicate node id '" + id + "'");
        }
        ComputeNode node = new ComputeNode(id, module, op, kernel);
        byId.put(id, node);
        nodes.add(node);
        finalized = false;
        return node;
    }

    /** Compatibility overload for the pre-kernel op API. */
    @Deprecated
    public ComputeNode addNode(String id, String module, OpTensor op) {
        return addNode(id, module, TensorKernels.spec(op),
                TensorKernels.legacy(op));
    }

    public ComputeNode addPlaceholder(String output, String id) {
        OpSpec op = new OpSpec("placeholder", List.of(), List.of(output), Map.of());
        return addNode(id, "@placeholder", op, TensorKernels.create(op));
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
            if ("placeholder".equals(node.opType())) {
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

        RunState state = new RunState(nodes, ctx);
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
                node.execute(ctx.store());
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
        }

        long wall = System.nanoTime() - state.execStartNanos;
        reportRunSummary(ctx, state, wall, 1);
    }

    /**
     * Asynchronous execution through a dependency-counter ready queue. A node
     * is submitted to {@code pool} only after all dependencies completed; its
     * op body runs directly on the pool thread. Blocks until every node
     * completed.
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
        RunState state = new RunState(nodes, ctx);
        state.execStartNanos = System.nanoTime();

        state.start(pool);
        state.await();

        long wall = System.nanoTime() - state.execStartNanos;
        reportRunSummary(ctx, state, wall, workers);
    }

    private static void runNode(ComputeNode node, ExecutorService pool,
                                ExecutionContext ctx, RunState state) {
        long start = System.nanoTime();
        try {
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
                node.execute(ctx.store());
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
        } catch (Throwable fatal) {
            node.setStatus(ComputeNode.Status.FAILED);
            node.setError(unwrap(fatal));
        } finally {
            if (state.markCompleted(node)) {
                state.completeOne();
                state.releaseDependents(node);
            }
            state.pump(pool);
        }
    }

    private void resetRunState() {
        for (ComputeNode node : nodes) {
            node.resetRunState();
        }
    }

    private void reserveOutputs(TensorStore store) {
        for (ComputeNode node : nodes) {
            store.ensureNames(node.opSpec().outputs());
        }
    }

    private static int workers(ExecutorService pool) {
        return pool instanceof ThreadPoolExecutor executor
                ? Math.max(1, executor.getMaximumPoolSize())
                : Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    private static void emitNode(ExecutionContext ctx, String action,
                                 ComputeNode node, long elapsedNanos, boolean ok) {
        if (!ctx.hasMonitor()) {
            return;
        }
        Monitor monitor = ctx.monitor();
        try {
            monitor.emit(new Event("column", action, node.id())
                    .attr("op", node.opType())
                    .attr("module", node.module())
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
        try {
            return node.fallback(ctx.store(), error);
        } catch (Throwable ignored) {
            return false;
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

    private static final class ModuleAcc {
        private long startNanos;
        private long endNanos;
        private int count;
        private boolean started;
    }

    private static final class RunState {
        private final ExecutionContext ctx;
        private final ComputeNode[] nodes;
        private final IdentityHashMap<ComputeNode, Integer> index =
                new IdentityHashMap<>();
        private final AtomicIntegerArray pending;
        private final int[][] dependentIndices;
        private final ConcurrentLinkedQueue<ComputeNode> ready =
                new ConcurrentLinkedQueue<>();
        private final AtomicBoolean pumping = new AtomicBoolean(false);
        private final AtomicBoolean aborted = new AtomicBoolean(false);
        private final Set<ComputeNode> completed =
                ConcurrentHashMap.newKeySet();
        private final AtomicInteger remaining;
        private final CountDownLatch done = new CountDownLatch(1);
        private final Map<String, ModuleAcc> modules = new ConcurrentHashMap<>();
        private final AtomicLong busyNanos = new AtomicLong();
        private long execStartNanos;

        private RunState(List<ComputeNode> graphNodes, ExecutionContext ctx) {
            this.ctx = ctx;
            int count = graphNodes.size();
            this.nodes = graphNodes.toArray(ComputeNode[]::new);
            this.pending = new AtomicIntegerArray(count);
            this.remaining = new AtomicInteger(count);

            for (int i = 0; i < count; i++) {
                ComputeNode node = nodes[i];
                index.put(node, i);
                pending.set(i, node.dependencyView().size());
            }

            int[] outDegree = new int[count];
            for (int i = 0; i < count; i++) {
                for (ComputeNode dependency : nodes[i].dependencyView()) {
                    outDegree[indexOf(dependency)]++;
                }
            }
            this.dependentIndices = new int[count][];
            for (int i = 0; i < count; i++) {
                dependentIndices[i] = new int[outDegree[i]];
            }
            int[] cursor = new int[count];
            for (int i = 0; i < count; i++) {
                for (ComputeNode dependency : nodes[i].dependencyView()) {
                    int dependencyIndex = indexOf(dependency);
                    dependentIndices[dependencyIndex][cursor[dependencyIndex]++] = i;
                }
            }

            for (int i = 0; i < count; i++) {
                if (pending.get(i) == 0) {
                    ready.add(nodes[i]);
                }
            }
        }

        private int indexOf(ComputeNode node) {
            Integer value = index.get(node);
            if (value == null) {
                throw new IllegalStateException(
                        "ComputeGraph: dependency outside this graph: " + node.id());
            }
            return value;
        }

        private void start(ExecutorService pool) {
            pump(pool);
        }

        private void pump(ExecutorService pool) {
            if (!pumping.compareAndSet(false, true)) {
                return;
            }
            try {
                while (!aborted.get()) {
                    ComputeNode node = ready.poll();
                    if (node == null) {
                        pumping.set(false);
                        // Close the enqueue-during-drain race: a producer that
                        // observed pumping == true will not pump again.
                        if (ready.isEmpty()
                                || !pumping.compareAndSet(false, true)) {
                            return;
                        }
                        continue;
                    }
                    try {
                        pool.execute(() -> runNode(node, pool, ctx, this));
                    } catch (RejectedExecutionException rejected) {
                        // No pool capacity: drain serially on this thread
                        // instead of leaving the graph stuck.
                        runNode(node, pool, ctx, this);
                    }
                }
                pumping.set(false);
            } catch (Throwable fatal) {
                pumping.set(false);
                failRemaining(fatal);
            }
        }

        private void failRemaining(Throwable fatal) {
            aborted.set(true);
            ready.clear();
            for (ComputeNode node : nodes) {
                if (!markCompleted(node)) {
                    continue;
                }
                node.setStatus(ComputeNode.Status.FAILED);
                node.setError(unwrap(fatal));
                completeOne();
            }
        }

        private boolean markCompleted(ComputeNode node) {
            return completed.add(node);
        }

        private void releaseDependents(ComputeNode node) {
            for (int dependentIndex : dependentIndices[indexOf(node)]) {
                if (pending.decrementAndGet(dependentIndex) == 0) {
                    ready.add(nodes[dependentIndex]);
                }
            }
        }

        private ModuleAcc module(String name) {
            return modules.computeIfAbsent(name, key -> new ModuleAcc());
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
