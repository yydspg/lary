// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.FeedFetch;
import io.lark.column.Row;
import io.lark.column.Tensor;
import io.lark.column.TensorTable;
import io.lark.column.exec.ComputeGraph;
import io.lark.column.exec.ExecutionContext;
import io.lark.column.monitor.ColumnStatsCollector;
import io.lark.column.monitor.RunStats;
import io.lark.metric.CompositeMonitor;
import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Business-facing column pipeline: register {@link ColumnNode}s, feed rows,
 * compute, fetch rows.
 *
 * <p>The execution graph is compiled into hidden {@code ComputeNode} nodes.
 * Business code never constructs or touches a {@code ComputeNode}; it only sees
 * {@link ColumnNode} and {@link ColumnContext}.
 *
 * <p>{@link #compile(Collection)} / {@link #run(FeedStream, FetchStream)} can
 * prune unused branches when the required fetch columns are known.
 *
 * <pre>{@code
 * try (NodePipeline pipeline = new NodePipeline()) {
 *     pipeline.addNode(new FetchNode())
 *             .addNode(new ScoreNode())
 *             .addNode(new FilterNode());
 *     pipeline.compile();
 *     pipeline.feed(List.of("user_id"), rows);
 *     pipeline.compute();
 *     List<Row> score = pipeline.fetch(List.of("score"));
 * }
 * }</pre>
 */
public final class NodePipeline implements AutoCloseable {

    private final ColumnGraph nodeGraph = new ColumnGraph();
    private Monitor userMonitor;
    private final ColumnStatsCollector stats = new ColumnStatsCollector();
    private CompositeMonitor chaining;
    private int computeWorkers;

    private final ExecutionContext ctx = new ExecutionContext();
    private ComputeGraph graph = new ComputeGraph();
    private ExecutorService pool;
    private boolean compiled;
    private Set<String> requiredFeedInputs = Set.of();
    private List<String> compiledOutputs = List.of();

    public NodePipeline() {
        this(0);
    }

    public NodePipeline(int computeWorkers) {
        this.computeWorkers = computeWorkers > 0
                ? computeWorkers
                : Math.max(1, Runtime.getRuntime().availableProcessors());
        rebuildMonitor();
    }

    // ---- business graph -------------------------------------------------

    public NodePipeline addNode(ColumnNode node) {
        nodeGraph.add(node);
        compiled = false;
        return this;
    }

    /** Scan packages for {@link ColumnComponent}-annotated business nodes. */
    public NodePipeline scan(String... packages) {
        for (ColumnNode node : ColumnComponentScanner.scan(packages)) {
            addNode(node);
        }
        return this;
    }

    public NodePipeline addNodes(ColumnNode... nodes) {
        for (ColumnNode node : nodes) {
            addNode(node);
        }
        return this;
    }

    /** Explicit ordering edge when no column dependency implies it. */
    public NodePipeline dependsOn(String node, String dependency) {
        nodeGraph.dependsOn(node, dependency);
        compiled = false;
        return this;
    }

    public List<ColumnNode> nodes() {
        return nodeGraph.nodes();
    }

    public int nodeCount() {
        return nodeGraph.size();
    }

    /** Number of nodes in the last compiled (possibly pruned) execution graph. */
    public int compiledNodeCount() {
        return graph.size();
    }

    public Set<String> requiredFeedInputs() {
        return requiredFeedInputs;
    }

    // ---- configuration --------------------------------------------------

    /** Attach any monitor; an internal ColumnStatsCollector is always chained. */
    public NodePipeline setMonitor(Monitor monitor) {
        this.userMonitor = monitor;
        rebuildMonitor();
        return this;
    }

    public NodePipeline setComputeWorkers(int workers) {
        this.computeWorkers = workers > 0
                ? workers
                : Math.max(1, Runtime.getRuntime().availableProcessors());
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
        return this;
    }

    private void rebuildMonitor() {
        CompositeMonitor composite = new CompositeMonitor().add(stats);
        if (userMonitor != null) {
            composite.add(userMonitor);
        }
        chaining = composite;
        ctx.setMonitor(composite);
    }

    // ---- compile / run --------------------------------------------------

    /**
     * Compile the business graph into hidden ComputeNode execution nodes.
     * Called automatically before the first compute.
     */
    public void compile() {
        compile((Collection<String>) null);
    }

    /**
     * Compile the graph, pruning unused branches when fetch outputs are given.
     */
    public void compile(Collection<String> requiredOutputs) {
        ColumnGraph.Compiled compiledGraph = requiredOutputs == null
                || requiredOutputs.isEmpty()
                ? nodeGraph.compile()
                : nodeGraph.compile(Set.copyOf(requiredOutputs));
        ComputeGraph newGraph = new ComputeGraph();

        for (ColumnNode node : compiledGraph.order()) {
            newGraph.addNode(node.name(), node.name(), new BusinessNodeOp(node));
        }
        for (ColumnNode node : compiledGraph.order()) {
            for (ColumnNode dependency : compiledGraph.dependenciesOf(node)) {
                newGraph.addDependency(node.name(), dependency.name());
            }
        }
        newGraph.finalizeGraph();
        this.graph = newGraph;
        this.requiredFeedInputs = compiledGraph.feedInputs();
        this.compiledOutputs = compiledGraph.order().stream()
                .flatMap(node -> node.outputs().stream())
                .distinct()
                .toList();
        this.compiled = true;
    }

    public void feed(List<String> names, List<Row> rows) {
        feed(FeedStream.of(names, rows));
    }

    public void feed(FeedStream stream) {
        feed(FeedFetch.feedTable(stream.columns(), stream.rows()));
    }

    public void feed(TensorTable table) {
        ctx.beginPhase(ExecutionContext.RunPhase.FEED);
        emitStart("feed.start", "feed");
        ctx.setFeed(table);
        ctx.store().clear();
        ctx.store().addTable(table);
        ctx.endPhase();
        emitEnd("feed.end", "feed", ExecutionContext.RunPhase.FEED);
        if (compiled) {
            validateFeed(table);
        }
    }

    public void compute() {
        if (!compiled) {
            compile();
        }
        runCompiledGraph();
    }

    /** Compile with pruning to {@code requiredOutputs}, then execute. */
    public void compute(Collection<String> requiredOutputs) {
        compile(requiredOutputs);
        runCompiledGraph();
    }

    private void runCompiledGraph() {
        validateFeed(ctx.feed());
        if (pool == null) {
            pool = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name("lark-column-node-", 0).factory());
        }
        ctx.beginPhase(ExecutionContext.RunPhase.COMPUTE);
        emitStart("compute.start", "compute");
        graph.executeAsync(pool, ctx, computeWorkers);
        ctx.endPhase();
        emitEnd("compute.end", "compute", ExecutionContext.RunPhase.COMPUTE);
    }

    private void validateFeed(TensorTable table) {
        List<String> missing = requiredFeedInputs.stream()
                .filter(column -> !table.has(column))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "NodePipeline: missing feed column(s) " + missing
                            + "; required=" + requiredFeedInputs);
        }
    }

    public List<Row> fetch(FetchStream stream) {
        return fetch(stream.columns());
    }

    public List<Row> fetch(List<String> names) {
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch");
        TensorTable table = ctx.store().toTable(names);
        ctx.setFetch(table);
        List<Row> rows = FeedFetch.fetchRows(table, names);
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH);
        return rows;
    }

    /** Fetch every declared business-node output. */
    public List<Row> fetch() {
        return fetch(compiled ? compiledOutputs : outputColumns());
    }

    public TensorTable fetchTable(List<String> names) {
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch");
        TensorTable table = ctx.store().toTable(names);
        ctx.setFetch(table);
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH);
        return table;
    }

    public double fetchScalar(String name) {
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch");
        Tensor tensor = ctx.store().get(name);
        double value = FeedFetch.fetchScalar(tensor);
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH);
        return value;
    }

    /** feed + compute + fetch in one call. */
    public List<Row> run(List<String> feedNames, List<Row> feedRows,
                         List<String> fetchNames) {
        return run(FeedStream.of(feedNames, feedRows),
                FetchStream.of(fetchNames));
    }

    /** Feed + pruned compute + fetch. */
    public List<Row> run(FeedStream feedStream, FetchStream fetchStream) {
        compile(fetchStream.columns());
        feed(feedStream);
        compute();
        return fetch(fetchStream);
    }

    // ---- accessors ------------------------------------------------------

    public ExecutionContext context() {
        return ctx;
    }

    public RunStats stats() {
        return stats.stats();
    }

    /** All declared business-node output columns, in registration order. */
    public List<String> outputColumns() {
        List<String> out = new ArrayList<>();
        for (ColumnNode node : nodeGraph.nodes()) {
            out.addAll(node.outputs());
        }
        return out;
    }

    /** Outputs of the last compiled (possibly pruned) graph. */
    public List<String> compiledOutputColumns() {
        return compiledOutputs;
    }

    private void emitStart(String action, String subject) {
        chaining.emit(new Event("column", action, subject));
    }

    private void emitEnd(String action, String subject,
                         ExecutionContext.RunPhase phase) {
        chaining.emit(new Event("column", action, subject)
                .durationNanos(ctx.phaseElapsedNanos(phase)));
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
    }
}
