// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.biz.component.ColumnComponent;
import io.lark.column.biz.component.ColumnComponentScanner;
import io.lark.column.biz.flow.FeedStream;
import io.lark.column.biz.flow.FetchStream;
import io.lark.column.biz.operator.ColumnOperatorContext;
import io.lark.column.biz.schema.Column;

import io.lark.column.ColumnDefinitionException;
import io.lark.column.ColumnExecutionException;
import io.lark.column.ColumnFeedException;
import io.lark.column.ColumnFetchException;
import io.lark.column.context.ColumnGraphContext;
import io.lark.column.context.ColumnRunContext;
import io.lark.column.context.ExperimentContext;
import io.lark.column.data.Cell;
import io.lark.column.tensor.FeedFetch;
import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorTable;
import io.lark.column.exec.ExecutionContext;
import io.lark.column.monitor.ColumnStatsCollector;
import io.lark.column.monitor.RunStats;
import io.lark.metric.CompositeMonitor;
import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Business pipeline over {@link ColumnModule}s.
 *
 * <p>Modules are added by instance (or discovered with {@link #scan}); their
 * nodes are wired exclusively through {@link ColumnStream} references. The
 * pipeline generates node ids, validates module dependencies, builds the hidden
 * execution graph and supports pruning to requested fetch streams.
 *
 * <p>Phase methods are fluent: {@code pipeline.addAll(a, b).compile()
 * .feed(feed).compute();}. The pipeline is mutable and is not thread-safe;
 * create one instance per concurrent run. Compilation produces an immutable
 * {@code PipelinePlan}, so repeated runs with the same requested outputs reuse
 * the graph.
 */
public final class ColumnPipeline implements AutoCloseable {

    private final List<ColumnModule> modules = new ArrayList<>();
    private final Map<Class<? extends ColumnModule>, ColumnModule> modulesByType =
            new LinkedHashMap<>();
    private final ColumnStatsCollector stats = new ColumnStatsCollector();
    private final ExecutionContext ctx = new ExecutionContext();
    private final ColumnNodeRegistry nodeRegistry = new ColumnNodeRegistry();

    private Monitor userMonitor;
    private CompositeMonitor chaining;
    private int computeWorkers = Math.max(1,
            Runtime.getRuntime().availableProcessors());
    private boolean virtualComputeThreads;
    private ExperimentContext experiment = ExperimentContext.empty();
    private ColumnRunContext runContext = ColumnRunContext.empty();
    private ColumnGraphContext graphContext = ColumnGraphContext.empty();

    private PipelinePlan plan;
    private List<String> planRequestedOutputs;
    private ExecutorService pool;

    public ColumnPipeline() {
        rebuildMonitor();
    }

    public ColumnPipeline(int computeWorkers) {
        setComputeWorkers(computeWorkers);
        rebuildMonitor();
    }

    // ---- configuration --------------------------------------------------

    public ColumnPipeline add(ColumnModule module) {
        Objects.requireNonNull(module, "module");
        module.setNodeRegistry(nodeRegistry);
        module.setGraphContext(graphContext);
        module.initialize();
        if (modulesByType.putIfAbsent(module.getClass(), module) != null) {
            throw new ColumnDefinitionException(
                    "ColumnPipeline: duplicate module type " + module.getClass().getName());
        }
        modules.add(module);
        plan = null;
        planRequestedOutputs = null;
        return this;
    }

    /** Add several modules in dependency order. */
    public ColumnPipeline addAll(ColumnModule... modules) {
        Objects.requireNonNull(modules, "modules");
        return addAll(List.of(modules));
    }

    /** Add a collection of modules in dependency order. */
    public ColumnPipeline addAll(Collection<? extends ColumnModule> modules) {
        Objects.requireNonNull(modules, "modules");
        List<ColumnModule> ordered = dependencyOrder(modules);
        for (ColumnModule module : ordered) {
            module.setNodeRegistry(nodeRegistry);
            module.setGraphContext(graphContext);
        }
        for (ColumnModule module : ordered) {
            add(module);
        }
        return this;
    }

    /**
     * Order modules so explicit {@code dependsOn} producers build first. A
     * missing dependency or a cycle falls back to insertion order and is
     * reported by {@link PipelineCompiler}.
     */
    private static List<ColumnModule> dependencyOrder(
            Collection<? extends ColumnModule> modules) {
        List<ColumnModule> input = List.copyOf(modules);
        Map<Class<?>, ColumnModule> byType = new LinkedHashMap<>();
        for (ColumnModule module : input) {
            if (byType.putIfAbsent(module.getClass(), module) != null) {
                return input;
            }
        }
        Map<Class<?>, Integer> inDegree = new LinkedHashMap<>();
        Map<Class<?>, List<Class<?>>> outgoing = new LinkedHashMap<>();
        for (ColumnModule module : input) {
            inDegree.put(module.getClass(), 0);
        }
        for (ColumnModule module : input) {
            for (Class<?> dependency : module.dependencyView()) {
                if (!byType.containsKey(dependency)) {
                    return input;
                }
                outgoing.computeIfAbsent(dependency, key -> new ArrayList<>())
                        .add(module.getClass());
                inDegree.merge(module.getClass(), 1, Integer::sum);
            }
        }
        java.util.ArrayDeque<Class<?>> ready = new java.util.ArrayDeque<>();
        for (Map.Entry<Class<?>, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                ready.add(entry.getKey());
            }
        }
        List<ColumnModule> ordered = new ArrayList<>(input.size());
        while (!ready.isEmpty()) {
            Class<?> type = ready.poll();
            ordered.add(byType.get(type));
            for (Class<?> next : outgoing.getOrDefault(type, List.of())) {
                int degree = inDegree.merge(next, -1, Integer::sum);
                if (degree == 0) {
                    ready.add(next);
                }
            }
        }
        return ordered.size() == input.size() ? ordered : input;
    }

    /**
     * Dependency-free discovery of {@link ColumnComponent} modules.
     *
     * <p>Modules must have a no-argument constructor. For Spring constructor
     * injection use the optional {@code :spring} module instead.
     */
    public ColumnPipeline scan(String... packages) {
        return addAll(ColumnComponentScanner.scanModules(packages));
    }

    public ColumnPipeline setMonitor(Monitor monitor) {
        this.userMonitor = monitor;
        rebuildMonitor();
        return this;
    }

    /**
     * Set the compile-time graph context. Configure it before adding modules;
     * modules are initialized once with this context.
     */
    public ColumnPipeline setGraphContext(ColumnGraphContext graphContext) {
        Objects.requireNonNull(graphContext, "graphContext");
        if (!modules.isEmpty() && this.graphContext != graphContext) {
            throw new ColumnDefinitionException(
                    "ColumnPipeline: setGraphContext before adding modules");
        }
        this.graphContext = graphContext;
        return this;
    }

    public ColumnGraphContext graphContext() {
        return graphContext;
    }

    public ColumnPipeline setComputeWorkers(int workers) {
        this.computeWorkers = workers > 0 ? workers
                : Math.max(1, Runtime.getRuntime().availableProcessors());
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
        return this;
    }

    /** Install per-run experiment assignments and parameters. */
    public ColumnPipeline setExperiment(ExperimentContext experiment) {
        this.experiment = Objects.requireNonNull(experiment, "experiment");
        return this;
    }

    public ExperimentContext experiment() {
        return experiment;
    }

    /**
     * Install a typed business context for the next run. The graph definition
     * stays context-free; custom operators read this through
     * {@code ColumnOperatorContext.runContext()}.
     */
    public ColumnPipeline setRunContext(ColumnRunContext runContext) {
        this.runContext = Objects.requireNonNull(runContext, "runContext");
        return this;
    }

    /** Convenience: build a run context keyed by the runtime class of each value. */
    public ColumnPipeline setRunContext(Object... values) {
        return setRunContext(ColumnRunContext.of(values));
    }

    public ColumnRunContext runContext() {
        return runContext;
    }

    /**
     * Use virtual threads instead of the fixed platform pool. The ready queue
     * still submits only dependency-ready nodes, but blocking nodes no longer
     * occupy one of a small number of platform workers. {@link #setComputeWorkers}
     * remains the concurrency hint used by monitoring.
     */
    public ColumnPipeline setVirtualComputeThreads(boolean virtualThreads) {
        if (this.virtualComputeThreads != virtualThreads) {
            this.virtualComputeThreads = virtualThreads;
            if (pool != null) {
                pool.shutdownNow();
                pool = null;
            }
        }
        return this;
    }

    public boolean usesVirtualComputeThreads() {
        return virtualComputeThreads;
    }

    private void rebuildMonitor() {
        CompositeMonitor composite = new CompositeMonitor().add(stats);
        if (userMonitor != null) {
            composite.add(userMonitor);
        }
        chaining = composite;
        ctx.setMonitor(composite);
    }

    // ---- compile --------------------------------------------------------

    /**
     * Compose all added modules into the execution graph. Modules declare only
     * their own subgraph with {@code build()}; this is the graph-building step.
     */
    public ColumnPipeline buildGraph() {
        return compile();
    }

    /** Compose/prune the graph for the typed fetch request. */
    public ColumnPipeline buildGraph(FetchStream requiredOutputs) {
        return compile(requiredOutputs);
    }

    /** Compile all module outputs and return this pipeline for fluent use. */
    public ColumnPipeline compile() {
        return compile((Collection<String>) null);
    }

    /** Prune to the typed streams in a fetch request. */
    public ColumnPipeline compile(FetchStream requiredOutputs) {
        return compile(requiredOutputs == null ? null : requiredOutputs.names());
    }

    /**
     * Compile modules/nodes into the hidden execution graph.
     *
     * <p>A failed recompile leaves the previous plan in place.
     *
     * @param requiredOutputs module output names to keep; empty/null keeps all
     */
    public ColumnPipeline compile(Collection<String> requiredOutputs) {
        List<String> requested = requiredOutputs == null || requiredOutputs.isEmpty()
                ? null : List.copyOf(requiredOutputs);
        if (plan != null && Objects.equals(planRequestedOutputs, requested)) {
            return this;
        }
        nodeRegistry.validateAcyclic();
        plan = PipelineCompiler.compile(modules, requested);
        planRequestedOutputs = requested;
        return this;
    }

    // ---- feed / compute / fetch ----------------------------------------

    /** Feed one or more typed external streams. Rows contain one cell per stream. */
    public ColumnPipeline feed(FeedStream stream) {
        Objects.requireNonNull(stream, "stream");
        return feed(FeedFetch.feedTable(stream.names(), stream.rows()));
    }

    public ColumnPipeline feed(List<String> names, List<Row> rows) {
        return feed(FeedFetch.feedTable(names, rows));
    }

    /**
     * Feed rows in the compiled required-column order.
     *
     * <p>The order comes from {@link #compile()}, so callers do not need to
     * repeat the module input list at the feed boundary.
     */
    public ColumnPipeline feedRows(List<Row> rows) {
        if (plan == null) {
            compile();
        }
        return feed(plan.requiredFeedColumnOrder(), rows);
    }

    /** Feed already column-oriented data, keyed by column name. */
    public ColumnPipeline feed(Map<String, List<Cell>> columns) {
        return feed(FeedFetch.feedTable(columns));
    }

    /**
     * Feed pre-built tensors without a row/cell transpose.
     *
     * <p>The pipeline takes ownership of these tensors; do not mutate them
     * after feeding.
     */
    public ColumnPipeline feedTensors(Map<String, Tensor> columns) {
        Objects.requireNonNull(columns, "columns");
        TensorTable table = new TensorTable();
        columns.forEach(table::add);
        return feed(table);
    }

    private ColumnPipeline feed(TensorTable table) {
        coerceFeed(table, false);
        Map<String, String> shape = Map.of(
                "rows", Integer.toString(table.numRows()),
                "columns", Integer.toString(table.numCols()));
        ctx.beginPhase(ExecutionContext.RunPhase.FEED);
        emitStart("feed.start", "feed", shape);
        ctx.setFeed(table);
        ctx.store().clear();
        // The table was built for this run and ownership transfers to the
        // store: avoid cloning every input column.
        ctx.store().addTable(table, false);
        ctx.endPhase();
        emitEnd("feed.end", "feed", ExecutionContext.RunPhase.FEED, shape);
        if (plan != null) {
            validateFeed(table);
        }
        return this;
    }

    public ColumnPipeline feed(String name, List<Row> rows) {
        return feed(List.of(name), rows);
    }

    public ColumnPipeline compute() {
        if (plan == null) {
            compile();
        }
        runCompiled();
        return this;
    }

    public ColumnPipeline compute(Collection<String> requiredOutputs) {
        compile(requiredOutputs);
        runCompiled();
        return this;
    }

    /** Typed pruning + compute convenience. */
    public ColumnPipeline compute(FetchStream requiredOutputs) {
        compile(requiredOutputs);
        runCompiled();
        return this;
    }

    private void runCompiled() {
        PipelinePlan current = plan;
        if (current == null) {
            throw new ColumnExecutionException("ColumnPipeline: compile first");
        }
        coerceFeed(ctx.feed(), true);
        validateFeed(ctx.feed());
        ctx.setExperiment(experiment);
        ctx.setRunContext(runContext);
        if (pool == null) {
            if (virtualComputeThreads) {
                // Blocking nodes: one virtual thread per ready node. The ready
                // queue still avoids parked dependency-waiting threads.
                pool = Executors.newThreadPerTaskExecutor(
                        Thread.ofVirtual()
                                .name("lark-column-worker-", 0)
                                .factory());
            } else {
                // Column kernels are CPU-bound: use a fixed platform pool so
                // the configured worker count is a real concurrency bound. The
                // ready queue only submits nodes whose dependencies are done.
                pool = Executors.newFixedThreadPool(computeWorkers,
                        Thread.ofPlatform()
                                .name("lark-column-worker-", 0)
                                .daemon(true)
                                .factory());
            }
        }
        Map<String, String> mode = Map.of(
                "workers", Integer.toString(computeWorkers),
                "virtual", Boolean.toString(virtualComputeThreads),
                "nodes", Integer.toString(current.nodeCount()));
        ctx.beginPhase(ExecutionContext.RunPhase.COMPUTE);
        emitStart("compute.start", "compute", mode);
        current.graph().executeAsync(pool, ctx, computeWorkers);
        ctx.endPhase();
        emitEnd("compute.end", "compute", ExecutionContext.RunPhase.COMPUTE, mode);
    }

    /**
     * Coerce pre-built columns to their declared feed dtypes. If the feed was
     * supplied before compile, the store already holds the tensors, so update
     * both the feed table and the store.
     */
    private void coerceFeed(TensorTable table, boolean updateStore) {
        PipelinePlan current = plan;
        if (current == null) {
            return;
        }
        for (Map.Entry<String, ColumnStream<?>> entry
                : current.feedColumnsByName().entrySet()) {
            String name = entry.getKey();
            if (!table.has(name)) {
                continue;
            }
            Tensor tensor = table.get(name);
            if (tensor.dtype() != entry.getValue().dtype()) {
                Tensor cast = tensor.castTo(entry.getValue().dtype());
                table.set(name, cast);
                if (updateStore) {
                    ctx.store().set(name, cast);
                }
            }
        }
    }

    private void validateFeed(TensorTable table) {
        PipelinePlan current = plan;
        if (current == null) {
            return;
        }
        List<String> missing = null;
        for (String column : current.requiredFeedColumnOrder()) {
            if (!table.has(column)) {
                if (missing == null) {
                    missing = new ArrayList<>(4);
                }
                missing.add(column);
            }
        }
        if (missing != null) {
            throw new ColumnFeedException(
                    "ColumnPipeline: missing feed column(s) " + missing
                            + "; required=" + current.requiredFeedColumns());
        }
    }

    /**
     * Row-oriented fetch. Shaped tensors are flattened into rows; use
     * {@link #fetchTensor} / {@link #fetchTensors} for logical shapes.
     */
    public List<Row> fetch(FetchStream stream) {
        return fetch(stream.names());
    }

    /** Row-oriented fetch; shaped tensors are flattened into rows. */
    public List<Row> fetch(List<String> names) {
        return FeedFetch.fetchRows(fetchTable(names), names);
    }

    /** Fetch a column-oriented view keyed by column name. */
    public Map<String, List<Cell>> fetchColumns(FetchStream stream) {
        return fetchColumns(stream.names());
    }

    /** Fetch selected tensors as flat cell lists, one list per column. */
    public Map<String, List<Cell>> fetchColumns(List<String> names) {
        Map<String, Tensor> tensors = fetchTensors(names);
        Map<String, List<Cell>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Tensor> entry : tensors.entrySet()) {
            result.put(entry.getKey(), FeedFetch.cells(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    /** Fetch a column-oriented view keyed by column name. */
    public Map<String, List<Cell>> fetchColumns(ColumnStream<?>... columns) {
        return fetchColumns(FetchStream.of(columns));
    }

    /**
     * Fetch every compiled output as flat cell lists. Unlike the row-oriented
     * no-arg {@link #fetch()}, this permits outputs with different shapes.
     */
    public Map<String, List<Cell>> fetchColumns() {
        Map<String, List<Cell>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Tensor> entry : fetchTensors().entrySet()) {
            result.put(entry.getKey(), FeedFetch.cells(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    /** Fetch every compiled output as a typed tensor view. */
    public Map<String, Tensor> fetchTensors() {
        if (plan == null) {
            throw new ColumnExecutionException("ColumnPipeline: compile first");
        }
        return fetchTensors(plan.outputs());
    }

    /** Fetch selected tensors without converting them to rows. */
    public Map<String, Tensor> fetchTensors(FetchStream stream) {
        return fetchTensors(stream.names());
    }

    /** Fetch selected tensors without converting them to rows. */
    public Map<String, Tensor> fetchTensors(List<String> names) {
        PipelinePlan current = plan;
        if (current == null) {
            throw new ColumnExecutionException("ColumnPipeline: compile first");
        }
        if (!current.canFetch(names)) {
            throw new ColumnFetchException(
                    "ColumnPipeline: cannot fetch " + names
                            + "; compiled outputs=" + current.outputs());
        }
        int rows = names.isEmpty() ? 0
                : ctx.store().get(names.get(0)).shape().dim(0);
        Map<String, String> shape = Map.of(
                "rows", Integer.toString(rows),
                "columns", Integer.toString(names.size()));
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch", shape);
        Map<String, Tensor> result = new LinkedHashMap<>();
        for (String name : names) {
            result.put(name, ctx.store().get(name));
        }
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH, shape);
        return Collections.unmodifiableMap(result);
    }

    private TensorTable fetchTable(List<String> names) {
        PipelinePlan current = plan;
        if (current == null) {
            throw new ColumnExecutionException("ColumnPipeline: compile first");
        }
        if (!current.canFetch(names)) {
            throw new ColumnFetchException(
                    "ColumnPipeline: cannot fetch " + names
                            + "; compiled outputs=" + current.outputs());
        }
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch");
        // Fetch only reads the columns; do not copy every result tensor.
        TensorTable table = ctx.store().toTable(names, false);
        ctx.setFetch(table);
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH,
                Map.of("rows", Integer.toString(table.numRows()),
                        "columns", Integer.toString(table.numCols())));
        return table;
    }

    public List<Row> fetch(String... names) {
        return fetch(List.of(names));
    }

    public List<Row> fetch(ColumnStream<?>... columns) {
        return fetch(FetchStream.of(columns));
    }

    public List<Row> fetch() {
        if (plan == null) {
            throw new ColumnExecutionException("ColumnPipeline: compile first");
        }
        return fetch(plan.outputs());
    }

    public double fetchScalar(ColumnStream<?> column) {
        return fetchScalar(column.name());
    }

    /**
     * Zero-copy typed result for columnar consumers. The returned tensor is a
     * read-only view; do not mutate it.
     */
    public Tensor fetchTensor(ColumnStream<?> column) {
        return fetchTensor(column.name());
    }

    /**
     * Zero-copy typed result for columnar consumers. The returned tensor is a
     * read-only view; do not mutate it.
     */
    public Tensor fetchTensor(String name) {
        return fetchTensors(List.of(name)).get(name);
    }

    public double fetchScalar(String name) {
        PipelinePlan current = plan;
        if (current == null || !current.outputSet().contains(name)) {
            throw new ColumnFetchException(
                    "ColumnPipeline: '" + name + "' is not a compiled output");
        }
        Map<String, String> shape = Map.of("rows", "1", "columns", "1");
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch", shape);
        double value = FeedFetch.fetchScalar(ctx.store().get(name));
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH, shape);
        return value;
    }

    /**
     * Compile, feed and compute in one call, then leave the results available
     * through the typed fetch methods. This is the normal run path for a graph
     * whose outputs have different shapes:
     *
     * <pre>{@code
     * try (ColumnPipeline pipeline = buildGraph()) {
     *     pipeline.setExperiment(experiment)
     *             .execute(feed, ColumnRunContext.of(request));
     *     List<Row> ranked = pipeline.fetch(FetchStream.of(goodsId, selected));
     * }
     * }</pre>
     */
    public ColumnPipeline execute(FeedStream feedStream) {
        return execute(feedStream, ColumnRunContext.empty());
    }

    public ColumnPipeline execute(FeedStream feedStream,
                                  ColumnRunContext runContext) {
        setRunContext(runContext);
        compile();
        feed(feedStream);
        compute();
        return this;
    }

    /** Columnar run path: compile, feed tensors and compute in one call. */
    public ColumnPipeline executeTensors(Map<String, Tensor> columns) {
        return executeTensors(columns, ColumnRunContext.empty());
    }

    public ColumnPipeline executeTensors(Map<String, Tensor> columns,
                                         ColumnRunContext runContext) {
        return executeTensors(columns, null, runContext);
    }

    public ColumnPipeline executeTensors(Map<String, Tensor> columns,
                                         FetchStream requiredOutputs,
                                         ColumnRunContext runContext) {
        setRunContext(runContext);
        compile(requiredOutputs);
        feedTensors(columns);
        compute();
        return this;
    }

    /** Compile/prune, feed and compute in one call. */
    public ColumnPipeline execute(FeedStream feedStream,
                                  FetchStream requiredOutputs) {
        return execute(feedStream, requiredOutputs, ColumnRunContext.empty());
    }

    public ColumnPipeline execute(FeedStream feedStream,
                                  FetchStream requiredOutputs,
                                  ColumnRunContext runContext) {
        setRunContext(runContext);
        compile(requiredOutputs);
        feed(feedStream);
        compute();
        return this;
    }

    /**
     * Single-shot run returning every compiled module output. All outputs must
     * share one row count; use {@link #fetchColumns()} or typed runs for
     * heterogeneous result shapes.
     */
    public List<Row> run(FeedStream feedStream) {
        compile();
        feed(feedStream);
        compute();
        return fetch();
    }

    /** Single-shot run pruned to the requested fetch streams. */
    public List<Row> run(FeedStream feedStream, FetchStream fetchStream) {
        compile(fetchStream);
        feed(feedStream);
        compute();
        return fetch(fetchStream);
    }

    /** Typed convenience for a single-shot feed/compute/fetch cycle. */
    public List<Row> run(FeedStream feedStream, ColumnStream<?>... outputs) {
        return run(feedStream, FetchStream.of(outputs));
    }

    // ---- accessors ------------------------------------------------------

    public ExecutionContext context() {
        return ctx;
    }

    public RunStats stats() {
        return stats.stats();
    }

    public List<ColumnModule> modules() {
        return List.copyOf(modules);
    }

    public <T extends ColumnModule> T module(Class<T> type) {
        ColumnModule module = modulesByType.get(type);
        if (module == null) {
            throw new ColumnDefinitionException(
                    "ColumnPipeline: module not added: " + type.getName());
        }
        return type.cast(module);
    }

    public int nodeCount() {
        int count = 0;
        for (ColumnModule module : modules) {
            count += module.nodeView().length;
        }
        return count;
    }

    public int compiledNodeCount() {
        return plan == null ? 0 : plan.nodeCount();
    }

    public Set<String> requiredFeedColumns() {
        return plan == null ? Set.of() : plan.requiredFeedColumns();
    }

    private void emitStart(String action, String subject) {
        emitStart(action, subject, Map.of());
    }

    private void emitStart(String action, String subject,
                           Map<String, String> attrs) {
        Event event = new Event("column", action, subject);
        attrs.forEach(event::attr);
        chaining.emit(event);
    }

    private void emitEnd(String action, String subject,
                         ExecutionContext.RunPhase phase) {
        emitEnd(action, subject, phase, Map.of());
    }

    private void emitEnd(String action, String subject,
                         ExecutionContext.RunPhase phase,
                         Map<String, String> attrs) {
        Event event = new Event("column", action, subject)
                .durationNanos(ctx.phaseElapsedNanos(phase));
        attrs.forEach(event::attr);
        chaining.emit(event);
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
    }
}
