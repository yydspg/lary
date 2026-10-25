// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;
import java.util.Objects;

/**
 * Business transformation node: multiply a column by a business weight.
 *
 * <p>Example: {@code new ScaleNode("weighted", "score", "weighted_score", 0.3)}.
 */
public final class ScaleNode extends ColumnNode {

    private final String name;
    private final String input;
    private final String output;
    private final double weight;

    public ScaleNode(String name, String input, String output, double weight) {
        this.name = Objects.requireNonNull(name, "name");
        this.input = Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");
        this.weight = weight;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<String> inputs() {
        return List.of(input);
    }

    @Override
    public List<String> outputs() {
        return List.of(output);
    }

    @Override
    public void execute(ColumnContext ctx) {
        Tensor value = ctx.input(input);
        ctx.output(output, Compute.mulScalar(value, weight));
    }
}
