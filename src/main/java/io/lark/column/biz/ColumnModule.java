// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Business module: a group of nodes with external feed streams, fetch streams,
 * and class-reference dependencies on other modules.
 *
 * <p>Nodes, inputs, outputs and dependencies are exposed as arrays; their
 * cardinality is fixed after module construction.
 *
 * <pre>{@code
 * final class FeatureModule extends ColumnModule {
 *     final ColumnStream userId = input("user_id");
 *     final FetchUserNode fetch = add(new FetchUserNode(userId));
 *     final ScoreNode score = add(new ScoreNode(fetch.out));
 *     { output(score.out); }
 * }
 * }</pre>
 */
public abstract class ColumnModule {

    private final String id = NodeIds.next(getClass());
    private final List<ColumnNode> nodeBuilder = new ArrayList<>();
    private final List<ColumnStream> inputBuilder = new ArrayList<>();
    private final List<ColumnStream> outputBuilder = new ArrayList<>();
    private final List<Class<? extends ColumnModule>> dependencyBuilder = new ArrayList<>();
    private final Map<String, ColumnStream> inputByName = new LinkedHashMap<>();

    private ColumnNode[] nodes;
    private ColumnStream[] inputs;
    private ColumnStream[] outputs;
    private Class<? extends ColumnModule>[] dependencies;
    private boolean initialized;

    /** Declare an external feed input stream (row data). */
    protected final ColumnStream input(String name) {
        return inputByName.computeIfAbsent(name, key -> {
            ColumnStream stream = new ColumnStream(key, null);
            inputBuilder.add(stream);
            inputs = null;
            return stream;
        });
    }

    /** Register a business node inside this module. */
    protected final <T extends ColumnNode> T add(T node) {
        nodeBuilder.add(Objects.requireNonNull(node, "node"));
        nodes = null;
        return node;
    }

    /** Expose node streams as module fetch outputs. */
    protected final void output(ColumnStream... streams) {
        for (ColumnStream stream : streams) {
            outputBuilder.add(Objects.requireNonNull(stream, "stream"));
        }
        outputs = null;
    }

    /** Class-reference dependency on another module (ordering, no data flow). */
    protected final void dependsOn(Class<? extends ColumnModule> module) {
        dependencyBuilder.add(Objects.requireNonNull(module, "module"));
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

    public final ColumnStream[] inputs() {
        if (inputs == null) {
            inputs = inputBuilder.toArray(ColumnStream[]::new);
        }
        return inputs;
    }

    public final ColumnStream[] outputs() {
        if (outputs == null) {
            outputs = outputBuilder.toArray(ColumnStream[]::new);
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
