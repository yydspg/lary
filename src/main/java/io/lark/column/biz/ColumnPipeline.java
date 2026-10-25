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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

    private ComputeGraph graph = new ComputeGraph();
    private ExecutorService pool;
    private boolean compiled;
    private Set<String> requiredFeedColumns = Set.of();
    private Map<String, ColumnStream<?>> fetchableOutputs = Map.of();
    private List<String> compiledOutputs = List.of();

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
        compiled = false;
        return this;
    }

    /** Spring-style discovery of {@link ColumnComponent} modules. */
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

    private void rebuildMonitor() {
        CompositeMonitor composite = new CompositeMonitor().add(stats);
        if (userMonitor != null) {
            composite.add(userMonitor);
        }
        chaining = composite;
        ctx.setMonitor(composite);
    }

    // ---- compile --------------------------------------------------------

    public void compile() {
        compile((Collection<String>) null);
    }

    /** Prune to the typed streams in a fetch request. */
    public void compile(FetchStream requiredOutputs) {
        compile(requiredOutputs == null ? null : requiredOutputs.names());
    }

    /**
     * Compile modules/nodes into the hidden execution graph.
     *
     * @param requiredOutputs module output names to keep; empty/null keeps all
     */
    public void compile(Collection<String> requiredOutputs) {
        for (ColumnModule module : modules) {
            module.initialize();
        }
        validateModuleDependencies();

        Map<ColumnNode, ColumnModule> nodeModules = new IdentityHashMap<>();
        Map<String, ColumnNode> producers = new LinkedHashMap<>();
        Map<ColumnStream<?>, ColumnModule> moduleInputs = new IdentityHashMap<>();

        for (ColumnModule module : modules) {
            for (ColumnStream<?> input : module.inputs()) {
                moduleInputs.put(input, module);
            }
            for (ColumnNode node : module.nodes()) {
                if (nodeModules.put(node, module) != null) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: node added to multiple modules " + node.id());
                }
                for (ColumnStream<?> fetch : node.fetches()) {
                    ColumnNode previous = producers.putIfAbsent(fetch.name(), node);
                    if (previous != null && previous != node) {
                        throw new IllegalArgumentException(
                                "ColumnPipeline: stream '" + fetch.name()
                                        + "' produced by both " + previous.id()
                                        + " and " + node.id());
                    }
                }
            }
        }

        Map<ColumnNode, List<ColumnNode>> deps = new IdentityHashMap<>();
        Set<String> neededFeedColumns = new LinkedHashSet<>();
        for (ColumnNode node : nodeModules.keySet()) {
            deps.put(node, new ArrayList<>());
            for (ColumnStream<?> feed : node.feeds()) {
                ColumnNode producer = feed.producer().orElse(null);
                if (producer != null) {
                    if (!nodeModules.containsKey(producer)) {
                        throw new IllegalArgumentException(
                                "ColumnPipeline: stream '" + feed.name()
                                        + "' of node " + node.id()
                                        + " has a producer outside this pipeline");
                    }
                    deps.get(node).add(producer);
                } else {
                    if (!moduleInputs.containsKey(feed)) {
                        throw new IllegalArgumentException(
                                "ColumnPipeline: stream '" + feed.name()
                                        + "' of node " + node.id()
                                        + " is not declared as a module input");
                    }
                    neededFeedColumns.add(feed.name());
                }
            }
        }

        Map<String, ColumnStream<?>> allOutputs = new LinkedHashMap<>();
        for (ColumnModule module : modules) {
            for (ColumnStream<?> output : module.outputs()) {
                ColumnStream<?> previous = allOutputs.putIfAbsent(output.name(), output);
                if (previous != null && previous != output) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: duplicate module output stream '"
                                    + output.name() + "'");
                }
            }
        }

        Set<ColumnNode> kept;
        List<String> outputs;
        if (requiredOutputs == null || requiredOutputs.isEmpty()) {
            kept = new LinkedHashSet<>(nodeModules.keySet());
            outputs = List.copyOf(allOutputs.keySet());
        } else {
            outputs = List.copyOf(requiredOutputs);
            kept = requiredNodes(outputs, allOutputs, producers, deps,
                    neededFeedColumns, moduleInputs);
        }

        Set<String> feedColumns = new LinkedHashSet<>();
        for (ColumnNode node : kept) {
            for (ColumnStream<?> feed : node.feeds()) {
                if (feed.producer().isEmpty()) {
                    feedColumns.add(feed.name());
                }
            }
        }
        for (String output : outputs) {
            ColumnStream<?> stream = allOutputs.get(output);
            if (stream != null && stream.producer().isEmpty()) {
                feedColumns.add(stream.name());
            }
        }

        ComputeGraph newGraph = new ComputeGraph();
        for (ColumnNode node : kept) {
            ColumnModule module = nodeModules.get(node);
            newGraph.addNode(node.id(), module.id(), new BusinessNodeOp(node));
        }
        for (ColumnNode node : kept) {
            for (ColumnNode dependency : deps.get(node)) {
                if (kept.contains(dependency)) {
                    newGraph.addDependency(node.id(), dependency.id());
                }
            }
        }
        newGraph.finalizeGraph();

        this.graph = newGraph;
        this.requiredFeedColumns = Set.copyOf(feedColumns);
        this.fetchableOutputs = Map.copyOf(allOutputs);
        this.compiledOutputs = List.copyOf(outputs);
        this.compiled = true;
    }

    private void validateModuleDependencies() {
        Map<Class<? extends ColumnModule>, Integer> inDegree = new LinkedHashMap<>();
        Map<Class<? extends ColumnModule>, List<Class<? extends ColumnModule>>> outgoing =
                new LinkedHashMap<>();
        for (ColumnModule module : modules) {
            inDegree.put(module.getClass(), 0);
        }
        for (ColumnModule module : modules) {
            for (Class<? extends ColumnModule> dependency : module.dependencies()) {
                if (!modulesByType.containsKey(dependency)) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: module " + module.getClass().getName()
                                    + " depends on missing module "
                                    + dependency.getName());
                }
                outgoing.computeIfAbsent(dependency, key -> new ArrayList<>())
                        .add(module.getClass());
                inDegree.merge(module.getClass(), 1, Integer::sum);
            }
        }
        Deque<Class<? extends ColumnModule>> ready = new ArrayDeque<>();
        for (Map.Entry<Class<? extends ColumnModule>, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                ready.add(entry.getKey());
            }
        }
        int visited = 0;
        while (!ready.isEmpty()) {
            Class<? extends ColumnModule> type = ready.poll();
            visited++;
            for (Class<? extends ColumnModule> next : outgoing.getOrDefault(type, List.of())) {
                int degree = inDegree.merge(next, -1, Integer::sum);
                if (degree == 0) {
                    ready.add(next);
                }
            }
        }
        if (visited != modules.size()) {
            throw new IllegalStateException("ColumnPipeline: module dependency cycle detected");
        }
    }

    private static Set<ColumnNode> requiredNodes(
            List<String> requiredOutputs,
            Map<String, ColumnStream<?>> allOutputs,
            Map<String, ColumnNode> producers,
            Map<ColumnNode, List<ColumnNode>> deps,
            Set<String> feedColumns,
            Map<ColumnStream<?>, ColumnModule> moduleInputs) {
        Set<ColumnNode> needed = new LinkedHashSet<>();
        Deque<ColumnNode> pending = new ArrayDeque<>();

        for (String output : requiredOutputs) {
            ColumnStream<?> stream = allOutputs.get(output);
            if (stream == null) {
                throw new IllegalArgumentException(
                        "ColumnPipeline: unknown fetch output '" + output + "'");
            }
            ColumnNode producer = stream.producer().orElse(null);
            if (producer == null) {
                if (!moduleInputs.containsKey(stream)) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: fetch output '" + output
                                    + "' is not a module output or input");
                }
                feedColumns.add(stream.name());
                continue;
            }
            if (needed.add(producer)) {
                pending.push(producer);
            }
        }

        while (!pending.isEmpty()) {
            ColumnNode node = pending.pop();
            for (ColumnNode dependency : deps.get(node)) {
                if (needed.add(dependency)) {
                    pending.push(dependency);
                }
            }
        }
        return needed;
    }

    // ---- feed / compute / fetch ----------------------------------------

    /** Feed one or more typed external streams. Rows contain one cell per stream. */
    public void feed(FeedStream stream) {
        Objects.requireNonNull(stream, "stream");
        TensorTable table = FeedFetch.feedTable(stream.names(), stream.rows());
        for (ColumnStream<?> column : stream.streams()) {
            Tensor tensor = table.get(column.name());
            if (tensor.dtype() != column.dtype()) {
                table.set(column.name(), tensor.castTo(column.dtype()));
            }
        }
        feed(table);
    }

    public void feed(List<String> names, List<Row> rows) {
        feed(FeedFetch.feedTable(names, rows));
    }

    private void feed(TensorTable table) {
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

    public void feed(String name, List<Row> rows) {
        feed(List.of(name), rows);
    }

    public void compute() {
        if (!compiled) {
            compile();
        }
        runCompiled();
    }

    public void compute(Collection<String> requiredOutputs) {
        compile(requiredOutputs);
        runCompiled();
    }

    private void runCompiled() {
        validateFeed(ctx.feed());
        if (pool == null) {
            pool = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name("lark-column-module-", 0).factory());
        }
        ctx.beginPhase(ExecutionContext.RunPhase.COMPUTE);
        emitStart("compute.start", "compute");
        graph.executeAsync(pool, ctx, computeWorkers);
        ctx.endPhase();
        emitEnd("compute.end", "compute", ExecutionContext.RunPhase.COMPUTE);
    }

    private void validateFeed(TensorTable table) {
        List<String> missing = requiredFeedColumns.stream()
                .filter(column -> !table.has(column))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "ColumnPipeline: missing feed column(s) " + missing
                            + "; required=" + requiredFeedColumns);
        }
    }

    public List<Row> fetch(FetchStream stream) {
        return fetch(stream.names());
    }

    public List<Row> fetch(List<String> names) {
        if (!compiled) {
            throw new IllegalStateException("ColumnPipeline: compile first");
        }
        if (!compiledOutputs.containsAll(names)) {
            throw new IllegalArgumentException(
                    "ColumnPipeline: cannot fetch " + names
                            + "; compiled outputs=" + compiledOutputs);
        }
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch");
        TensorTable table = ctx.store().toTable(names);
        ctx.setFetch(table);
        List<Row> rows = FeedFetch.fetchRows(table, names);
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH);
        return rows;
    }

    public List<Row> fetch(String... names) {
        return fetch(List.of(names));
    }

    public List<Row> fetch(ColumnStream<?>... columns) {
        return fetch(FetchStream.of(columns));
    }

    public List<Row> fetch() {
        return fetch(compiledOutputs);
    }

    public double fetchScalar(ColumnStream<?> column) {
        return fetchScalar(column.name());
    }

    public double fetchScalar(String name) {
        if (!compiledOutputs.contains(name)) {
            throw new IllegalArgumentException(
                    "ColumnPipeline: '" + name + "' is not a compiled output");
        }
        ctx.beginPhase(ExecutionContext.RunPhase.FETCH);
        emitStart("fetch.start", "fetch");
        double value = FeedFetch.fetchScalar(ctx.store().get(name));
        ctx.endPhase();
        emitEnd("fetch.end", "fetch", ExecutionContext.RunPhase.FETCH);
        return value;
    }

    public List<Row> run(FeedStream feedStream, FetchStream fetchStream) {
        compile(fetchStream);
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
            count += module.nodes().length;
        }
        return count;
    }

    public int compiledNodeCount() {
        return graph.size();
    }

    public Set<String> requiredFeedColumns() {
        return requiredFeedColumns;
    }

    public List<String> outputColumns() {
        List<String> names = new ArrayList<>();
        for (ColumnModule module : modules) {
            for (ColumnStream<?> stream : module.outputs()) {
                if (!names.contains(stream.name())) {
                    names.add(stream.name());
                }
            }
        }
        return List.copyOf(names);
    }

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
