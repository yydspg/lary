// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;
import java.util.Objects;

/**
 * Business transformation node: convert a numeric unit by a factor.
 *
 * <p>Example: fen → li is factor 10:
 * {@code new UnitConvertNode("fen_to_li", "amount_fen", "amount_li", 10.0)}.
 */
public final class UnitConvertNode extends ColumnNode {

    private final String name;
    private final String input;
    private final String output;
    private final double factor;

    public UnitConvertNode(String name, String input, String output, double factor) {
        this.name = Objects.requireNonNull(name, "name");
        this.input = Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");
        this.factor = factor;
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
        ctx.output(output, Compute.mulScalar(value, factor));
    }
}
