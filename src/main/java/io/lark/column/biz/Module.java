// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.op.OpSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unit of business computation: declares inputs, its sub-graph and outputs.
 * It deliberately knows nothing about how it is wired into the global graph.
 */
public final class Module {

    private final String name;
    private final List<String> inputs = new ArrayList<>();
    private final List<String> outputs = new ArrayList<>();
    private final List<String> dependencies = new ArrayList<>();
    private final SubGraph subgraph = new SubGraph();
    private String dslSource;

    public Module(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public Module input(String port) {
        inputs.add(Objects.requireNonNull(port, "port"));
        return this;
    }

    public Module output(String port) {
        outputs.add(Objects.requireNonNull(port, "port"));
        return this;
    }

    /** Force an ordering edge onto another module (no data flow required). */
    public Module dependsOn(String moduleName) {
        dependencies.add(Objects.requireNonNull(moduleName, "moduleName"));
        return this;
    }

    public Module op(OpSpec spec) {
        subgraph.op(spec);
        return this;
    }

    public Module fromDsl(String source) {
        Objects.requireNonNull(source, "source");
        this.dslSource = source;
        for (OpSpec spec : ColumnDsl.parse(source)) {
            subgraph.op(spec);
        }
        return this;
    }

    public String name() {
        return name;
    }

    public List<String> inputs() {
        return List.copyOf(inputs);
    }

    public List<String> outputs() {
        return List.copyOf(outputs);
    }

    public List<String> dependencies() {
        return List.copyOf(dependencies);
    }

    public SubGraph subgraph() {
        return subgraph;
    }

    public boolean hasDsl() {
        return dslSource != null;
    }

    public String dsl() {
        return dslSource == null ? "" : dslSource;
    }
}
