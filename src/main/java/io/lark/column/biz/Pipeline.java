// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.FeedFetch;
import io.lark.column.Row;
import io.lark.column.Tensor;
import io.lark.column.TensorTable;
import io.lark.column.backend.Backend;
import io.lark.column.backend.CpuBackend;
import io.lark.column.exec.ComputeGraph;
import io.lark.column.exec.ExecutionContext;
import io.lark.column.monitor.ColumnStatsCollector;
import io.lark.column.monitor.RunStats;
import io.lark.metric.CompositeMonitor;
import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Top-level business orchestrator: feed → compute → fetch.
 *
 * <p>Modules are joined into a global graph by the framework using anonymous
 * boundary / temp / placeholder nodes, so a module never knows how it is wired
 * or whether its inputs are ready.
 */
public final class Pipeline implements AutoCloseable {

    private Backend backend;
    private final List<Module> modules = new ArrayList<>();
    private final Set<String> moduleNames = new HashSet<>();
    private Monitor userMonitor;
    private final ColumnStatsCollector stats = new ColumnStatsCollector();
    private CompositeMonitor chaining;
    private int computeWorkers;

    private final ExecutionContext ctx = new ExecutionContext();
    private ComputeGraph graph = new ComputeGraph();
    private ExecutorService pool;
    private boolean compiled;

    public Pipeline() {
        this(new CpuBackend(), 0);
    }

    public Pipeline(Backend backend) {
        this(backend, 0);
    }

    public Pipeline(Backend backend, int computeWorkers) {
        this.backend = Objects.requireNonNull(backend, "backend");
        this.computeWorkers = computeWorkers > 0
                ? computeWorkers
                : Math.max(1, Runtime.getRuntime().availableProcessors());
        rebuildMonitor();
    }

    // ---- configuration --------------------------------------------------

    public Pipeline addModule(Module module) {
        Objects.requireNonNull(module, "module");
        if (!moduleNames.add(module.name())) {
            throw new IllegalArgumentException(
                    "Pipeline.addModule: duplicate module '" + module.name() + "'");
        }
        modules.add(module);
        compiled = false;
        return this;
    }

    public Pipeline setBackend(Backend backend) {
        this.backend = Objects.requireNonNull(backend, "backend");
        compiled = false;
        return this;
    }

    /** Attach any monitor; an internal ColumnStatsCollector is always chained. */
    public Pipeline setMonitor(Monitor monitor) {
        this.userMonitor = monitor;
        rebuildMonitor();
        return this;
    }

    public Pipeline setComputeWorkers(int workers) {
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

    // ---- compile --------------------------------------------------------

    /**
     * Compile registered modules into the global compute graph. Called
     * automatically before the first compute.
     */
    public void compile() {
        graph = new ComputeGraph();
        PipelineCompiler compiler = new PipelineCompiler(graph, backend);
        compiler.compile(modules, moduleNames);
        graph.finalizeGraph();
        compiled = true;
    }

    // ---- feed / compute / fetch ----------------------------------------

    public void feed(List<String> names, List<Row> rows) {
        feed(FeedFetch.feedTable(names, rows));
    }

    public void feed(TensorTable table) {
        ctx.beginPhase(ExecutionContext.RunPhase.FEED);
        emitStart("feed.start", "feed");
        ctx.setFeed(table);
        ctx.store().clear();
        ctx.store().addTable(table);
        ctx.endPhase();
        emitEnd("feed.end", "feed", ExecutionContext.RunPhase.FEED);
    }

    public void compute() {
        if (!compiled) {
            compile();
        }
        if (pool == null) {
            pool = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name("lark-column-", 0).factory());
        }
        ctx.beginPhase(ExecutionContext.RunPhase.COMPUTE);
        emitStart("compute.start", "compute");
        graph.executeAsync(pool, ctx, computeWorkers);
        ctx.endPhase();
        emitEnd("compute.end", "compute", ExecutionContext.RunPhase.COMPUTE);
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

    /** Fetch every declared module output. */
    public List<Row> fetch() {
        return fetch(outputColumns());
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
        feed(feedNames, feedRows);
        compute();
        return fetch(fetchNames);
    }

    // ---- accessors ------------------------------------------------------

    public ExecutionContext context() {
        return ctx;
    }

    public ComputeGraph graph() {
        return graph;
    }

    public int nodeCount() {
        return graph.size();
    }

    public int moduleCount() {
        return modules.size();
    }

    public RunStats stats() {
        return stats.stats();
    }

    /** All declared module output columns, in registration order. */
    public List<String> outputColumns() {
        List<String> out = new ArrayList<>();
        for (Module module : modules) {
            out.addAll(module.outputs());
        }
        return out;
    }

    public List<Module> modules() {
        return List.copyOf(modules);
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
