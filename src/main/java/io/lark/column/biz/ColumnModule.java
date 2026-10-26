// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Business module: a group of nodes with typed external feed streams, fetch
 * outputs and class-reference dependencies on other modules.
 *
 * <p>Inputs and outputs are typed column handles, not strings:
 *
 * <pre>{@code
 * final class FunnelMetricModule extends ColumnModule {
 *     private ColumnStream<Long> impressions;
 *     private ColumnStream<Long> clicks;
 *     private ColumnStream<Double> ctr;
 *
 *     @Override protected void define() {
 *         impressions = feed(Metrics.IMPRESSIONS);
 *         clicks = feed(Metrics.CLICKS);
 *         ctr = node(ColumnOps.ratio(Metrics.CTR), clicks, impressions);
 *         output(ctr);
 *     }
 * }</pre>
 *
 * <p>Nodes, inputs, outputs and dependencies are exposed as array snapshots;
 * their cardinality is fixed after module construction. Internal compiler and
 * scheduler code uses package-private views. `feedMap()` and `fetchMap()` are
 * cached read-only name views; use `published(Column)` for typed
 * module-to-module wiring.
 */
public abstract class ColumnModule {

    private final String id = NodeIds.next(getClass());
    private final List<ColumnNode> nodeBuilder = new ArrayList<>();
    private final List<ColumnStream<?>> inputBuilder = new ArrayList<>();
    private final List<ColumnStream<?>> outputBuilder = new ArrayList<>();
    private final List<Class<? extends ColumnModule>> dependencyBuilder = new ArrayList<>();
    private final Map<String, ColumnStream<?>> inputByName = new LinkedHashMap<>();

    private enum InitState { NEW, INITIALIZING, READY }

    private ColumnNode[] nodes;
    private ColumnStream<?>[] inputs;
    private ColumnStream<?>[] outputs;
    private Class<? extends ColumnModule>[] dependencies;
    private InitState initState = InitState.NEW;
    private volatile Map<String, ColumnStream<?>> feedMap;
    private volatile Map<String, ColumnStream<?>> fetchMap;

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
                throw new IllegalArgumentException(
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
     * Define a node from a business-level computation op and typed inputs.
     * Inputs may be module feeds or other node output streams.
     */
    protected final <O> ColumnStream<O> node(ColumnOp<O> op,
                                             ColumnStream<?>... inputs) {
        return add(new OpNode<>(op, inputs)).out();
    }

    /** Register a business node inside this module. */
    protected final <T extends ColumnNode> T add(T node) {
        nodeBuilder.add(Objects.requireNonNull(node, "node"));
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
    protected final void dependsOn(Class<? extends ColumnModule>... modules) {
        for (Class<? extends ColumnModule> module : modules) {
            dependencyBuilder.add(Objects.requireNonNull(module, "module"));
        }
        dependencies = null;
    }

    /**
     * Define this module's node subgraph and feed/fetch streams.
     *
     * <p>Called once before compilation. A module creates nodes, wires them
     * from module feeds or other node outputs, and declares its own outputs:
     * this is the module's subgraph; the framework collects module subgraphs
     * into the full execution graph.
     */
    protected abstract void define();

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
                throw new IllegalArgumentException(
                        "ColumnModule " + getClass().getName() + ": output '"
                                + column.name() + "' has a different definition");
            }
            return (ColumnStream<T>) output;
        }
        throw new IllegalArgumentException(
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
        try {
            define();
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
    public final Class<? extends ColumnModule>[] dependencies() {
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

    @SuppressWarnings("unchecked")
    final Class<? extends ColumnModule>[] dependencyView() {
        if (dependencies == null) {
            dependencies = dependencyBuilder.toArray(Class[]::new);
        }
        return dependencies;
    }
}
