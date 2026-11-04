// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import io.lark.column.ColumnDefinitionException;
import io.lark.column.ColumnExecutionException;
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
import java.util.concurrent.CompletionException;
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

    private static final AtomicLong NEXT_INDEX = new AtomicLong();
    private final List<ComputeNode> nodes = new ArrayList<>();
    private final Map<String, ComputeNode> byId = new LinkedHashMap<>();
    private final Map<Long, ComputeNode> byIndex = new LinkedHashMap<>();
    private List<ComputeNode> topo = List.of();
    private Plan plan;
    private boolean finalized;

    public ComputeNode addNode(String id, String module, OpSpec op,
                               TensorKernel kernel) {
        return addNode(NEXT_INDEX.incrementAndGet(), id, module, op, kernel);
    }

    public ComputeNode addNode(long index, String id, String module, OpSpec op,
                               TensorKernel kernel) {
        Objects.requireNonNull(id, "id");
        if (byId.containsKey(id)) {
            throw new ColumnDefinitionException(
                    "ComputeGraph: duplicate node id '" + id + "'");
        }
        if (byIndex.containsKey(index)) {
            throw new ColumnDefinitionException(
                    "ComputeGraph: duplicate node index " + index);
        }
        ComputeNode node = new ComputeNode(id, module, op, kernel);
        node.setGlobalIndex(index);
        byId.put(id, node);
        byIndex.put(index, node);
        nodes.add(node);
        finalized = false;
        plan = null;
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
        byIndex.remove(placeholder.globalIndex());
        nodes.remove(placeholder);
        finalized = false;
        plan = null;
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
        plan = null;
    }

    /** Topologically sort and detect cycles (Kahn's algorithm). */
    public void finalizeGraph() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setGraphIndex(i);
        }
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
            throw new ColumnDefinitionException("ComputeGraph: dependency cycle detected");
        }

        topo = List.copyOf(order);
        plan = new Plan(nodes, topo);
        finalized = true;
    }

    /** Precomputed scheduler metadata for the finalized graph. */
    private Plan schedulerPlan() {
        if (!finalized) {
            finalizeGraph();
        }
        return plan;
    }

    /** Sequential execution in topological order on the calling thread. */
    public void execute(ExecutionContext ctx) {
        Objects.requireNonNull(ctx, "ctx");
        Plan plan = schedulerPlan();
        resetRunState();
        if (nodes.isEmpty()) {
            return;
        }
        reserveOutputs(ctx.store(), plan);

        RunState state = new RunState(plan, ctx);
        state.execStartNanos = System.nanoTime();

        for (ComputeNode node : plan.topo()) {
            long start = System.nanoTime();
            emitNode(ctx, "node.start", node, 0, true);
            state.startModule(node, start);

            Throwable error = null;
            try {
                node.execute(ctx.store(), ctx.experiment(), ctx.runContext());
            } catch (Throwable t) {
                error = t;
            }

            long elapsed = System.nanoTime() - start;
            node.setElapsedNanos(elapsed);
            state.busyNanos.addAndGet(elapsed);
            state.endModule(node, start, elapsed);
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
        Plan plan = schedulerPlan();
        resetRunState();
        if (nodes.isEmpty()) {
            return;
        }
        reserveOutputs(ctx.store(), plan);

        int workers = Math.max(1, workersHint);
        RunState state = new RunState(plan, ctx);
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
            state.startModule(node, start);

            Throwable error = null;
            try {
                node.execute(ctx.store(), ctx.experiment(), ctx.runContext());
            } catch (Throwable t) {
                error = unwrap(t);
            }

            long elapsed = System.nanoTime() - start;
            node.setElapsedNanos(elapsed);
            state.busyNanos.addAndGet(elapsed);
            state.endModule(node, start, elapsed);
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
            int completedIndex = node.graphIndex();
            if (state.markCompleted(completedIndex)) {
                state.completeOne();
                state.releaseDependents(completedIndex);
            }
            state.pump(pool);
        }
    }

    private void resetRunState() {
        for (ComputeNode node : nodes) {
            node.resetRunState();
        }
    }

    private void reserveOutputs(TensorStore store, Plan plan) {
        store.ensureNames(plan.outputs());
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
            String[] moduleNames = state.plan.moduleNames;
            for (int i = 0; i < moduleNames.length; i++) {
                ModuleAcc acc = state.modules[i];
                synchronized (acc) {
                    long duration = acc.started ? Math.max(0, acc.endNanos - acc.startNanos) : 0;
                    monitor.emit(new Event("column", "module.end", moduleNames[i])
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
            return node.fallback(ctx.store(), error, ctx.experiment(), ctx.runContext());
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

    /**
     * Immutable scheduler metadata built once when the graph is finalized.
     * Every run copies only the primitive dependency counters, not the
     * adjacency maps or reverse-edge arrays.
     */
    private static final class Plan {

        private final ComputeNode[] nodes;
        private final ComputeNode[] topo;
        private final int[] initialPending;
        private final int[][] dependentIndices;
        private final int[] roots;
        private final String[] outputs;
        private final String[] moduleNames;
        private final int[] nodeModule;

        private Plan(List<ComputeNode> nodes, List<ComputeNode> topo) {
            int count = nodes.size();
            this.nodes = nodes.toArray(ComputeNode[]::new);
            this.topo = topo.toArray(ComputeNode[]::new);
            this.initialPending = new int[count];
            this.dependentIndices = new int[count][];

            Map<String, Integer> moduleIndex = new LinkedHashMap<>();
            this.moduleNames = new String[moduleCount(nodes, moduleIndex)];
            this.nodeModule = new int[count];
            for (int i = 0; i < count; i++) {
                this.nodes[i].setGraphIndex(i);
                this.initialPending[i] = this.nodes[i].dependencyView().size();
                this.nodeModule[i] = moduleIndex.get(this.nodes[i].module());
            }

            int[] outDegree = new int[count];
            for (int i = 0; i < count; i++) {
                for (ComputeNode dependency : this.nodes[i].dependencyView()) {
                    outDegree[indexOf(dependency)]++;
                }
            }
            for (int i = 0; i < count; i++) {
                dependentIndices[i] = new int[outDegree[i]];
            }
            int[] cursor = new int[count];
            for (int i = 0; i < count; i++) {
                for (ComputeNode dependency : this.nodes[i].dependencyView()) {
                    int dependencyIndex = indexOf(dependency);
                    dependentIndices[dependencyIndex][cursor[dependencyIndex]++] = i;
                }
            }

            int rootCount = 0;
            for (int i = 0; i < count; i++) {
                if (initialPending[i] == 0) {
                    rootCount++;
                }
            }
            this.roots = new int[rootCount];
            int root = 0;
            for (int i = 0; i < count; i++) {
                if (initialPending[i] == 0) {
                    roots[root++] = i;
                }
            }

            int outputCount = 0;
            for (ComputeNode node : this.nodes) {
                outputCount += node.opSpec().outputs().size();
            }
            this.outputs = new String[outputCount];
            int output = 0;
            for (ComputeNode node : this.nodes) {
                for (String name : node.opSpec().outputs()) {
                    outputs[output++] = name;
                }
            }
            for (Map.Entry<String, Integer> entry : moduleIndex.entrySet()) {
                moduleNames[entry.getValue()] = entry.getKey();
            }
        }

        private static int moduleCount(List<ComputeNode> nodes,
                                       Map<String, Integer> moduleIndex) {
            for (ComputeNode node : nodes) {
                moduleIndex.computeIfAbsent(node.module(),
                        key -> moduleIndex.size());
            }
            return moduleIndex.size();
        }

        private int indexOf(ComputeNode node) {
            int value = node.graphIndex();
            if (value < 0 || value >= nodes.length) {
                throw new ColumnDefinitionException(
                        "ComputeGraph: dependency outside this graph: " + node.id());
            }
            return value;
        }

        ComputeNode[] nodes() {
            return nodes;
        }

        ComputeNode[] topo() {
            return topo;
        }

        int[] initialPending() {
            return initialPending.clone();
        }

        int[][] dependentIndices() {
            return dependentIndices;
        }

        int[] roots() {
            return roots;
        }

        String[] outputs() {
            return outputs;
        }
    }

    private static final class RunState {
        private final ExecutionContext ctx;
        private final Plan plan;
        private final ComputeNode[] nodes;
        private final AtomicIntegerArray pending;
        private final int[][] dependentIndices;
        private final ConcurrentLinkedQueue<ComputeNode> ready =
                new ConcurrentLinkedQueue<>();
        private final AtomicBoolean pumping = new AtomicBoolean(false);
        private final AtomicBoolean aborted = new AtomicBoolean(false);
        private final AtomicIntegerArray completed;
        private final AtomicInteger remaining;
        private final CountDownLatch done = new CountDownLatch(1);
        private final ModuleAcc[] modules;
        private final AtomicLong busyNanos = new AtomicLong();
        private long execStartNanos;

        private RunState(Plan plan, ExecutionContext ctx) {
            this.ctx = ctx;
            this.plan = plan;
            this.nodes = plan.nodes();
            int count = nodes.length;
            this.pending = new AtomicIntegerArray(plan.initialPending());
            this.remaining = new AtomicInteger(count);
            this.completed = new AtomicIntegerArray(count);
            this.dependentIndices = plan.dependentIndices();
            this.modules = new ModuleAcc[plan.moduleNames.length];
            for (int i = 0; i < modules.length; i++) {
                modules[i] = new ModuleAcc();
            }

            for (int root : plan.roots()) {
                ready.add(nodes[root]);
            }
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
            for (int i = 0; i < nodes.length; i++) {
                if (!markCompleted(i)) {
                    continue;
                }
                nodes[i].setStatus(ComputeNode.Status.FAILED);
                nodes[i].setError(unwrap(fatal));
                completeOne();
            }
        }

        private boolean markCompleted(int index) {
            return completed.compareAndSet(index, 0, 1);
        }

        private void releaseDependents(int index) {
            for (int dependentIndex : dependentIndices[index]) {
                if (pending.decrementAndGet(dependentIndex) == 0) {
                    ready.add(nodes[dependentIndex]);
                }
            }
        }

        private ModuleAcc module(ComputeNode node) {
            return modules[plan.nodeModule[node.graphIndex()]];
        }

        private void startModule(ComputeNode node, long startNanos) {
            ModuleAcc acc = module(node);
            synchronized (acc) {
                acc.count++;
                if (!acc.started) {
                    acc.started = true;
                    acc.startNanos = startNanos;
                }
            }
        }

        private void endModule(ComputeNode node, long startNanos, long elapsed) {
            ModuleAcc acc = module(node);
            synchronized (acc) {
                acc.endNanos = Math.max(acc.endNanos, startNanos + elapsed);
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
                throw new ColumnExecutionException(
                        "Interrupted while waiting for the compute graph", e);
            }
        }
    }
}
