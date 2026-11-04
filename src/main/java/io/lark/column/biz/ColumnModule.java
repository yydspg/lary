// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.biz.flow.FeedStream;
import io.lark.column.biz.operator.ColumnOperator;
import io.lark.column.biz.schema.Column;

import io.lark.column.ColumnDefinitionException;
import io.lark.column.context.ColumnGraphContext;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

/**
 * Business module: a group of nodes with typed external feed streams, fetch
 * outputs and class-reference dependencies on other modules.
 *
 * <p>Inputs and outputs are typed column handles, not strings:
 *
 * <pre>{@code
 * final class FunnelMetricModule extends ColumnModule<Object> {
 *     private ColumnStream<Long> impressions;
 *     private ColumnStream<Long> clicks;
 *     private ColumnStream<Double> ctr;
 *
 *     @Override protected void buildFeeds(ColumnGraphContext context) {
 *         impressions = feed(Metrics.IMPRESSIONS);
 *         clicks = feed(Metrics.CLICKS);
 *     }
 *
 *     @Override protected void buildNodes(ColumnGraphContext context) {
 *         ctr = node(Metrics.CTR, RatioOp.class, clicks, impressions);
 *     }
 *
 *     @Override protected void buildFetches(ColumnGraphContext context) {
 *         output(ctr);
 *     }
 * }</pre>
 *
 * <p>The three build phases are ordered: feeds, nodes, fetches. The pipeline
 * compiles the collected modules once, then handles feed, execution and fetch
 * per run.
 *
 * <p>Nodes, inputs, outputs and dependencies are exposed as array snapshots;
 * their cardinality is fixed after module construction. Internal compiler and
 * scheduler code uses package-private views. `feedMap()` and `fetchMap()` are
 * cached read-only name views; use `published(Column)` for typed
 * module-to-module wiring.
 */
public abstract class ColumnModule<F> {

    private final String id = NodeIds.next(getClass());
    private final List<ColumnNode> nodeBuilder = new ArrayList<>();
    private final List<ColumnStream<?>> inputBuilder = new ArrayList<>();
    private final List<ColumnStream<?>> outputBuilder = new ArrayList<>();
    private final List<Class<? extends ColumnModule<?>>> dependencyBuilder =
            new ArrayList<>();
    private final Map<String, ColumnStream<?>> inputByName = new LinkedHashMap<>();
    private final Map<ColumnStream<?>, FeedMapping.Value<Object>>
            feedMappingGetters = new LinkedHashMap<>();

    private enum InitState { NEW, INITIALIZING, READY }

    private ColumnNode[] nodes;
    private ColumnStream<?>[] inputs;
    private ColumnStream<?>[] outputs;
    private Class<? extends ColumnModule<?>>[] dependencies;
    private InitState initState = InitState.NEW;
    private ColumnGraphContext graphContext = ColumnGraphContext.empty();
    private volatile Map<String, ColumnStream<?>> feedMap;
    private volatile Map<String, ColumnStream<?>> fetchMap;
    private FeedMapping<Object> moduleFeedMapping;
    private ColumnNodeRegistry nodeRegistry = new ColumnNodeRegistry();

    /**
     * Declare a typed external feed input. The column definition carries the
     * name and dtype; repeated declarations of the same definition return the
     * same handle.
     */
    @SuppressWarnings("unchecked")
    protected final <T> ColumnStream<T> input(Column<T> column) {
        ColumnStream<?> existing = inputByName.get(column.name());
        if (existing != null) {
            if (!existing.definition().equals(column)) {
                throw new ColumnDefinitionException(
                        "ColumnModule: input '" + column.name()
                                + "' declared with conflicting definitions");
            }
            return (ColumnStream<T>) existing;
        }
        ColumnStream<T> stream = new ColumnStream<>(column, null);
        inputByName.put(column.name(), stream);
        inputBuilder.add(stream);
        inputs = null;
        feedMap = null;
        return stream;
    }

    /** Declarative alias for {@link #input(Column)}. */
    protected final <T> ColumnStream<T> feed(Column<T> column) {
        return input(column);
    }

    /**
     * Declare one feed column and its business-object getter in one call.
     *
     * <pre>{@code
     * pvId = feed(PV_ID, SearchRow::pvId);
     * gmvFen = feed(GMV_FEN, row -> row.goods().gmvFen());
     * }</pre>
     *
     * <p>When all feeds are declared this way, the module exposes
     * {@link #feedStream(List)} and the business never builds a separate
     * {@link FeedMapping}.
     */
    /** Primitive long feed getter: no boxing per row. */
    @SuppressWarnings("unchecked")
    protected final ColumnStream<Long> feed(
            Column<Long> column, ToLongFunction<? super F> getter) {
        Objects.requireNonNull(column, "column");
        Objects.requireNonNull(getter, "getter");
        ColumnStream<Long> stream = feed(column);
        feedMappingGetters.put(stream, new FeedMapping.Value<Object>() {
            @Override
            public long longValue(Object row) {
                return getter.applyAsLong((F) row);
            }

            @Override
            public double doubleValue(Object row) {
                return getter.applyAsLong((F) row);
            }
        });
        moduleFeedMapping = null;
        return stream;
    }

    /** Primitive double feed getter: no boxing per row. */
    @SuppressWarnings("unchecked")
    protected final ColumnStream<Double> feed(
            Column<Double> column, ToDoubleFunction<? super F> getter) {
        Objects.requireNonNull(column, "column");
        Objects.requireNonNull(getter, "getter");
        ColumnStream<Double> stream = feed(column);
        feedMappingGetters.put(stream, new FeedMapping.Value<Object>() {
            @Override
            public long longValue(Object row) {
                return (long) getter.applyAsDouble((F) row);
            }

            @Override
            public double doubleValue(Object row) {
                return getter.applyAsDouble((F) row);
            }
        });
        moduleFeedMapping = null;
        return stream;
    }


    /**
     * Map a business object list to this module's feed columns. All feeds must
     * have been declared with {@code feed(Column, getter)}.
     */
    public final FeedStream feedStream(List<? extends F> rows) {
        return moduleFeedMapping().map(rows);
    }

    /** Columnar feed path: no Cell/Row objects and no row transpose. */
    public final Map<String, Tensor> feedTensorMap(List<? extends F> rows) {
        return moduleFeedMapping().tensors(rows);
    }

    private FeedMapping<Object> moduleFeedMapping() {
        FeedMapping<Object> mapping = moduleFeedMapping;
        if (mapping != null) {
            return mapping;
        }
        if (feedMappingGetters.isEmpty()) {
            throw new ColumnDefinitionException(
                    "ColumnModule " + getClass().getName()
                            + ": no feed mapping declared; use"
                            + " feed(Column, getter)");
        }
        if (feedMappingGetters.size() != inputBuilder.size()) {
            throw new ColumnDefinitionException(
                    "ColumnModule " + getClass().getName()
                            + ": feed mapping covers "
                            + feedMappingGetters.size() + " of "
                            + inputBuilder.size() + " feed columns");
        }
        mapping = FeedMapping.of(
                new ArrayList<>(feedMappingGetters.keySet()),
                new ArrayList<>(feedMappingGetters.values()));
        moduleFeedMapping = mapping;
        return mapping;
    }

    /**
     * Define a node from a declarative operator class:
     *
     * <pre>{@code
     * ctr = node(SearchMetrics.CTR, RatioOp.class, clicks, impressions);
     * }</pre>
     *
     * <p>The class must have a no-arg constructor. Parameterized operators use
     * the instance overload instead.
     */
    protected final <O> ColumnStream<O> node(Column<O> output,
                                             Class<? extends ColumnOperator> operator,
                                             ColumnStream<?>... inputs) {
        return node(output, instantiate(operator), inputs);
    }

    /**
     * Define a node from a declarative operator instance, for operators that
     * carry configuration:
     *
     * <pre>{@code
     * couponCost = node(SearchMetrics.COUPON_COST,
     *         new ScaleOp(100.0), feed(SearchMetrics.COUPON_COST_FEN));
     * }</pre>
     *
     * <p>The operator result is coerced to the declared output dtype.
     */
    protected final <O> ColumnStream<O> node(Column<O> output,
                                             ColumnOperator operator,
                                             ColumnStream<?>... inputs) {
        Objects.requireNonNull(output, "output");
        Objects.requireNonNull(operator, "operator");
        int expected = operator.arity();
        if (expected >= 0 && expected != inputs.length) {
            throw new ColumnDefinitionException(
                    "Column operator " + operator.operatorName()
                            + " requires " + expected + " input(s), got "
                            + inputs.length);
        }
        ColumnStream<?> existing = nodeRegistry.output(output.name());
        if (existing != null) {
            return cast(existing, output);
        }
        ColumnStream<O> stream = add(new OpNode<>(output, operator, inputs)).out();
        nodeRegistry.registerOutput(stream);
        return stream;
    }

    /**
     * Anonymous double node: the output column is generated internally and is
     * not part of the module schema.
     *
     * <pre>{@code
     * var couponCost = node(new ScaleOp(100.0), couponCostFen);
     * var score = node(MulOp.class, baseScore, couponCost);
     * }</pre>
     */
    protected final ColumnStream<Double> node(ColumnOperator operator,
                                              ColumnStream<?>... inputs) {
        return node(DType.FLOAT64, operator, inputs);
    }

    /** Anonymous double node from an operator class. */
    protected final ColumnStream<Double> node(
            Class<? extends ColumnOperator> operator,
            ColumnStream<?>... inputs) {
        return node(DType.FLOAT64, instantiate(operator), inputs);
    }

    /** Anonymous INT64 node. */
    protected final ColumnStream<Long> nodeLong(ColumnOperator operator,
                                                ColumnStream<?>... inputs) {
        return node(DType.INT64, operator, inputs);
    }

    /** Anonymous INT64 node from an operator class. */
    protected final ColumnStream<Long> nodeLong(
            Class<? extends ColumnOperator> operator,
            ColumnStream<?>... inputs) {
        return node(DType.INT64, instantiate(operator), inputs);
    }

    /** Anonymous node with an explicit dtype. */
    protected final <O> ColumnStream<O> node(DType dtype,
                                             ColumnOperator operator,
                                             ColumnStream<?>... inputs) {
        ColumnNodeRegistry.NodeKey key = ColumnNodeRegistry.key(
                operator.operatorKey(), dtype, inputs);
        ColumnStream<?> existing = nodeRegistry.definition(key);
        if (existing != null) {
            @SuppressWarnings("unchecked")
            ColumnStream<O> shared = (ColumnStream<O>) existing;
            return shared;
        }
        Column<O> output = anonymous(dtype);
        ColumnStream<O> stream = node(output, operator, inputs);
        nodeRegistry.registerDefinition(key, stream);
        return stream;
    }

    @SuppressWarnings("unchecked")
    private static <O> Column<O> anonymous(DType dtype) {
        return (Column<O>) new Column<Object>(
                "_anon_" + NodeIds.next("anonymous"), dtype);
    }

    /** Register a business node inside this module. */
    protected final <T extends ColumnNode> T add(T node) {
        nodeBuilder.add(Objects.requireNonNull(node, "node"));
        for (ColumnStream<?> fetch : node.fetchView()) {
            nodeRegistry.registerOutput(fetch);
        }
        nodes = null;
        return node;
    }

    /** Declarative alias for {@link #add(ColumnNode)}. */
    protected final <T extends ColumnNode> T node(T node) {
        return add(node);
    }

    /** Register a no-arg unary node and bind its typed input. */
    protected final <N extends UnaryNode<I, O>,
            I extends ColumnStream<?>, O> N node(N node, I input) {
        node.bind(input);
        return add(node);
    }

    /** Register a no-arg binary node and bind its typed inputs. */
    protected final <N extends BinaryNode<A, B, O>,
            A extends ColumnStream<?>, B extends ColumnStream<?>, O>
            N node(N node, A left, B right) {
        node.bind(left, right);
        return add(node);
    }

    /** Expose typed node streams as module fetch outputs. */
    protected final void output(ColumnStream<?>... streams) {
        for (ColumnStream<?> stream : streams) {
            outputBuilder.add(Objects.requireNonNull(stream, "stream"));
        }
        outputs = null;
        fetchMap = null;
    }

    /** Declarative alias for {@link #output(ColumnStream...)}. */
    protected final void fetch(ColumnStream<?>... streams) {
        output(streams);
    }

    /** Class-reference dependencies on other modules (ordering, no data flow). */
    @SafeVarargs
    protected final void dependsOn(
            Class<? extends ColumnModule<?>>... modules) {
        for (Class<? extends ColumnModule<?>> module : modules) {
            dependencyBuilder.add(Objects.requireNonNull(module, "module"));
        }
        dependencies = null;
    }

    /**
     * Build this module's graph: declare feeds, nodes and fetch outputs.
     *
     * <p>Called once before compilation. This is the single graph-definition
     * hook: a module creates nodes, wires them from feeds or other node
     * outputs, and declares its own outputs. The framework collects module
     * graphs, prunes, executes and exposes the fetch streams; modules never
     * feed or execute themselves.
     */
    protected void build() {
        buildFeeds(graphContext);
        buildNodes(graphContext);
        buildFetches(graphContext);
        define();
    }

    /**
     * Phase 1: declare external feed streams. Runs before nodes and fetches.
     */
    protected void buildFeeds(ColumnGraphContext context) {
    }

    /**
     * Phase 2: declare computation nodes. Runs after feeds and before fetches.
     */
    protected void buildNodes(ColumnGraphContext context) {
    }

    /**
     * Phase 3: declare fetch outputs. Runs after nodes.
     */
    protected void buildFetches(ColumnGraphContext context) {
    }

    /**
     * Build this module's graph with a framework/business context. Override
     * this form when graph construction needs configuration:
     *
     * <pre>{@code
     * @Override protected void build(ColumnGraphContext context) {
     *     int k = (int) context.experiment().param("rerank.top_k", 30);
     *     ...
     * }
     * }</pre>
     *
     * <p>Per-request values still use {@code ColumnRunContext}; this context is
     * compile-time configuration only.
     */
    protected void build(ColumnGraphContext context) {
        build();
    }

    /**
     * Legacy graph-definition hook. New modules override {@link #build()};
     * this remains so existing modules keep working.
     */
    protected void define() {
    }

    /**
     * Resolve a required feed or shared node by its column definition.
     */
    @SuppressWarnings("unchecked")
    protected final <T> ColumnStream<T> required(Column<T> column) {
        Objects.requireNonNull(column, "column");
        ColumnStream<?> input = inputByName.get(column.name());
        if (input != null) {
            return cast(input, column);
        }
        ColumnStream<?> shared = nodeRegistry.output(column.name());
        if (shared != null) {
            return cast(shared, column);
        }
        throw new ColumnDefinitionException(
                "ColumnModule " + getClass().getName()
                        + ": required node '" + column.name()
                        + "' has not been defined; add the producer module"
                        + " first or declare dependsOn(...)");
    }

    /**
     * Fetch a shared node output by schema definition. The node may have been
     * created by this module or by another module in the same pipeline.
     */
    public final <T> ColumnStream<T> shared(Column<T> column) {
        initialize();
        ColumnStream<?> stream = nodeRegistry.output(
                Objects.requireNonNull(column, "column").name());
        if (stream == null) {
            throw new ColumnDefinitionException(
                    "ColumnModule " + getClass().getName()
                            + ": shared node '" + column.name()
                            + "' is not registered");
        }
        return cast(stream, column);
    }

    /** Declare fetch outputs by required node definitions. */
    protected final void fetch(Column<?>... columns) {
        ColumnStream<?>[] streams = new ColumnStream<?>[columns.length];
        for (int i = 0; i < columns.length; i++) {
            streams[i] = required(columns[i]);
        }
        output(streams);
    }

    @SuppressWarnings("unchecked")
    private static <T> ColumnStream<T> cast(ColumnStream<?> stream,
                                            Column<T> column) {
        if (!stream.definition().equals(column)) {
            throw new ColumnDefinitionException(
                    "ColumnModule: shared column '" + column.name()
                            + "' has a different definition");
        }
        return (ColumnStream<T>) stream;
    }

    /**
     * Look up one of this module's published fetch outputs by schema definition.
     *
     * <p>This is the typed form of {@link #fetchMap()} for module-to-module
     * wiring: the column name is still the runtime key, but the dtype is
     * checked and the returned handle needs no cast at the call site.
     */
    @SuppressWarnings("unchecked")
    public final <T> ColumnStream<T> published(Column<T> column) {
        Objects.requireNonNull(column, "column");
        initialize();
        for (ColumnStream<?> output : outputView()) {
            if (!output.name().equals(column.name())) {
                continue;
            }
            if (!output.definition().equals(column)) {
                throw new ColumnDefinitionException(
                        "ColumnModule " + getClass().getName() + ": output '"
                                + column.name() + "' has a different definition");
            }
            return (ColumnStream<T>) output;
        }
        throw new ColumnDefinitionException(
                "ColumnModule " + getClass().getName() + ": output '"
                        + column.name() + "' is not published");
    }

    /** Read-only view of this module's external feed handles, keyed by name. */
    public final Map<String, ColumnStream<?>> feedMap() {
        initialize();
        Map<String, ColumnStream<?>> cached = feedMap;
        if (cached == null) {
            Map<String, ColumnStream<?>> map = new LinkedHashMap<>();
            for (ColumnStream<?> input : inputView()) {
                map.put(input.name(), input);
            }
            cached = Collections.unmodifiableMap(map);
            feedMap = cached;
        }
        return cached;
    }

    /** Read-only view of this module's published fetch handles, keyed by name. */
    public final Map<String, ColumnStream<?>> fetchMap() {
        initialize();
        Map<String, ColumnStream<?>> cached = fetchMap;
        if (cached == null) {
            Map<String, ColumnStream<?>> map = new LinkedHashMap<>();
            for (ColumnStream<?> output : outputView()) {
                map.put(output.name(), output);
            }
            cached = Collections.unmodifiableMap(map);
            fetchMap = cached;
        }
        return cached;
    }

    /**
     * Idempotent module definition. Reentrant calls from {@link #define()} are
     * harmless; a failed definition is retried on the next call instead of
     * leaving the module half-initialized.
     */
    final synchronized void initialize() {
        if (initState != InitState.NEW) {
            return;
        }
        initState = InitState.INITIALIZING;
        int nodeCount = nodeBuilder.size();
        int inputCount = inputBuilder.size();
        int outputCount = outputBuilder.size();
        int dependencyCount = dependencyBuilder.size();
        Map<String, ColumnStream<?>> inputsBefore = new LinkedHashMap<>(inputByName);
        Map<ColumnStream<?>, FeedMapping.Value<Object>> feedMappingBefore =
                new LinkedHashMap<>(feedMappingGetters);
        try {
            build(graphContext);
            initState = InitState.READY;
        } catch (Throwable failure) {
            // A failed definition must not leave partial nodes/streams behind:
            // the next initialize() retries from the pre-define state.
            truncate(nodeBuilder, nodeCount);
            truncate(inputBuilder, inputCount);
            truncate(outputBuilder, outputCount);
            truncate(dependencyBuilder, dependencyCount);
            inputByName.clear();
            inputByName.putAll(inputsBefore);
            feedMappingGetters.clear();
            feedMappingGetters.putAll(feedMappingBefore);
            moduleFeedMapping = null;
            nodes = null;
            inputs = null;
            outputs = null;
            dependencies = null;
            feedMap = null;
            fetchMap = null;
            initState = InitState.NEW;
            throw failure;
        }
    }

    private static <T> void truncate(List<T> list, int size) {
        if (list.size() > size) {
            list.subList(size, list.size()).clear();
        }
    }

    final void setGraphContext(ColumnGraphContext context) {
        Objects.requireNonNull(context, "context");
        if (initState != InitState.NEW && graphContext != context) {
            throw new ColumnDefinitionException(
                    "ColumnModule " + getClass().getName()
                            + " was already built with a different graph context;"
                            + " create a new module instance");
        }
        graphContext = context;
    }

    final void setNodeRegistry(ColumnNodeRegistry registry) {
        Objects.requireNonNull(registry, "registry");
        if (nodeRegistry != registry) {
            registry.adopt(nodeRegistry);
            nodeRegistry = registry;
        }
    }

    private static ColumnOperator instantiate(
            Class<? extends ColumnOperator> type) {
        Objects.requireNonNull(type, "operator");
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            throw new ColumnDefinitionException(
                    "Column operator " + type.getName()
                            + " must be a concrete class");
        }
        String message = "Column operator " + type.getName()
                + " needs a public no-arg constructor; use the operator"
                + " instance form for parameterized operators";
        try {
            var constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException failure) {
            throw new ColumnDefinitionException(message, failure);
        } catch (InvocationTargetException failure) {
            throw new ColumnDefinitionException(message, failure.getCause());
        } catch (ReflectiveOperationException failure) {
            throw new ColumnDefinitionException(message, failure);
        }
    }

    public final String id() {
        return id;
    }

    /** Snapshot of registered nodes; callers cannot mutate module wiring. */
    public final ColumnNode[] nodes() {
        return nodeView().clone();
    }

    /** Snapshot of external feed handles. */
    public final ColumnStream<?>[] inputs() {
        return inputView().clone();
    }

    /** Snapshot of published output handles. */
    public final ColumnStream<?>[] outputs() {
        return outputView().clone();
    }

    /** Snapshot of module dependency classes. */
    @SuppressWarnings("unchecked")
    public final Class<? extends ColumnModule<?>>[] dependencies() {
        return dependencyView().clone();
    }

    final ColumnNode[] nodeView() {
        if (nodes == null) {
            nodes = nodeBuilder.toArray(ColumnNode[]::new);
        }
        return nodes;
    }

    final ColumnStream<?>[] inputView() {
        if (inputs == null) {
            inputs = inputBuilder.toArray(ColumnStream<?>[]::new);
        }
        return inputs;
    }

    final ColumnStream<?>[] outputView() {
        if (outputs == null) {
            outputs = outputBuilder.toArray(ColumnStream<?>[]::new);
        }
        return outputs;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    final Class<? extends ColumnModule<?>>[] dependencyView() {
        if (dependencies == null) {
            dependencies = dependencyBuilder.toArray(Class[]::new);
        }
        return dependencies;
    }
}
