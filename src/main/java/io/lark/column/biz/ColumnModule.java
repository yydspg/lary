// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Business module: a group of nodes with typed external feed streams, fetch
 * outputs and class-reference dependencies on other modules.
 *
 * <p>Inputs and outputs are typed column classes, not strings:
 *
 * <pre>{@code
 * final class FunnelMetricModule extends ColumnModule {
 *     final Impressions impressions = input(Impressions.class);
 *     final Clicks clicks = input(Clicks.class);
 *     final CtrNode ctr = add(new CtrNode(clicks, impressions));
 *     { output(ctr.out()); }
 * }
 * }</pre>
 *
 * <p>Nodes, inputs, outputs and dependencies are exposed as arrays; their
 * cardinality is fixed after module construction.
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
     * Declare a typed external feed input. The column class defines the name
     * and dtype; repeated declarations of the same class return the same
     * stream.
     */
    protected final <T extends ColumnStream<?>> T input(Class<T> type) {
        T candidate = ColumnStreams.create(type);
        String name = candidate.name();
        ColumnStream<?> existing = inputByName.get(name);
        if (existing != null) {
            if (!type.isInstance(existing)) {
                throw new IllegalArgumentException(
                        "ColumnModule: input '" + name + "' declared as both "
                                + existing.getClass().getName() + " and "
                                + type.getName());
            }
            return type.cast(existing);
        }
        inputByName.put(name, candidate);
        inputBuilder.add(candidate);
        inputs = null;
        return candidate;
    }

    /** Register a business node inside this module. */
    protected final <T extends ColumnNode> T add(T node) {
        nodeBuilder.add(Objects.requireNonNull(node, "node"));
        nodes = null;
        return node;
    }

    /** Expose typed node streams as module fetch outputs. */
    protected final void output(ColumnStream<?>... streams) {
        for (ColumnStream<?> stream : streams) {
            outputBuilder.add(Objects.requireNonNull(stream, "stream"));
        }
        outputs = null;
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
