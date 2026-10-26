// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.data.Cell;
import io.lark.column.data.FeedFetch;
import io.lark.column.data.Row;
import io.lark.column.data.Tensor;
import io.lark.column.data.TensorTable;
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

    private Monitor userMonitor;
    private CompositeMonitor chaining;
    private int computeWorkers = Math.max(1,
            Runtime.getRuntime().availableProcessors());
    private boolean virtualComputeThreads;

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
        module.initialize();
        if (modulesByType.putIfAbsent(module.getClass(), module) != null) {
            throw new IllegalArgumentException(
                    "ColumnPipeline: duplicate module type " + module.getClass().getName());
        }
        modules.add(module);
        plan = null;
        planRequestedOutputs = null;
        return this;
    }

    /** Add several modules in order. */
    public ColumnPipeline addAll(ColumnModule... modules) {
        Objects.requireNonNull(modules, "modules");
        for (ColumnModule module : modules) {
            add(module);
        }
        return this;
    }

    /** Add a collection of modules in iteration order. */
    public ColumnPipeline addAll(Collection<? extends ColumnModule> modules) {
        Objects.requireNonNull(modules, "modules");
        for (ColumnModule module : modules) {
            add(module);
        }
        return this;
    }

    /**
     * Dependency-free discovery of {@link ColumnComponent} modules.
     *
     * <p>Modules must have a no-argument constructor. For Spring constructor
     * injection use the optional {@code :spring} module instead.
     */
    public ColumnPipeline scan(String... packages) {
        for (ColumnModule module : ColumnComponentScanner.scanModules(packages)) {
            add(module);
        }
        return this;
    }

    public ColumnPipeline setMonitor(Monitor monitor) {
        this.userMonitor = monitor;
        rebuildMonitor();
        return this;
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
            throw new IllegalStateException("ColumnPipeline: compile first");
        }
        coerceFeed(ctx.feed(), true);
        validateFeed(ctx.feed());
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
        List<String> missing = current.requiredFeedColumns().stream()
                .filter(column -> !table.has(column))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
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
            throw new IllegalStateException("ColumnPipeline: compile first");
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
            throw new IllegalStateException("ColumnPipeline: compile first");
        }
        if (!current.canFetch(names)) {
            throw new IllegalArgumentException(
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
            throw new IllegalStateException("ColumnPipeline: compile first");
        }
        if (!current.canFetch(names)) {
            throw new IllegalArgumentException(
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
            throw new IllegalStateException("ColumnPipeline: compile first");
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
            throw new IllegalArgumentException(
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
            throw new IllegalArgumentException(
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
