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
 *     final ColumnStream<Long> impressions = feed(Metrics.IMPRESSIONS);
 *     final ColumnStream<Long> clicks = feed(Metrics.CLICKS);
 *     final CtrNode ctr = node(new CtrNode(clicks, impressions));
 *     { fetch(ctr.out()); }
 * }
 * }</pre>
 *
 * <p>Nodes, inputs, outputs and dependencies are exposed as arrays; their
 * cardinality is fixed after module construction. `feedMap()` and `fetchMap()`
 * are read-only name views; use `published(Column)` for typed module-to-module
 * wiring.
 */
public abstract class ColumnModule {

    private final String id = NodeIds.next(getClass());
    private final List<ColumnNode> nodeBuilder = new ArrayList<>();
    private final List<ColumnStream<?>> inputBuilder = new ArrayList<>();
    private final List<ColumnStream<?>> outputBuilder = new ArrayList<>();
    private final List<Class<? extends ColumnModule>> dependencyBuilder = new ArrayList<>();
    private final Map<String, ColumnStream<?>> inputByName = new LinkedHashMap<>();

    private ColumnNode[] nodes;
    private ColumnStream<?>[] inputs;
    private ColumnStream<?>[] outputs;
    private Class<? extends ColumnModule>[] dependencies;
    private boolean initialized;

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
        return stream;
    }

    /** Declarative alias for {@link #input(Column)}. */
    protected final <T> ColumnStream<T> feed(Column<T> column) {
        return input(column);
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

    /** Optional imperative definition hook. */
    protected void define() {
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
        for (ColumnStream<?> output : outputs()) {
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
        Map<String, ColumnStream<?>> map = new LinkedHashMap<>();
        for (ColumnStream<?> input : inputs()) {
            map.put(input.name(), input);
        }
        return Collections.unmodifiableMap(map);
    }

    /** Read-only view of this module's published fetch handles, keyed by name. */
    public final Map<String, ColumnStream<?>> fetchMap() {
        initialize();
        Map<String, ColumnStream<?>> map = new LinkedHashMap<>();
        for (ColumnStream<?> output : outputs()) {
            map.put(output.name(), output);
        }
        return Collections.unmodifiableMap(map);
    }

    final void initialize() {
        if (!initialized) {
            initialized = true;
            define();
        }
    }

    public final String id() {
        return id;
    }

    public final ColumnNode[] nodes() {
        if (nodes == null) {
            nodes = nodeBuilder.toArray(ColumnNode[]::new);
        }
        return nodes;
    }

    public final ColumnStream<?>[] inputs() {
        if (inputs == null) {
            inputs = inputBuilder.toArray(ColumnStream<?>[]::new);
        }
        return inputs;
    }

    public final ColumnStream<?>[] outputs() {
        if (outputs == null) {
            outputs = outputBuilder.toArray(ColumnStream<?>[]::new);
        }
        return outputs;
    }

    @SuppressWarnings("unchecked")
    public final Class<? extends ColumnModule>[] dependencies() {
        if (dependencies == null) {
            dependencies = dependencyBuilder.toArray(Class[]::new);
        }
        return dependencies;
    }
}
