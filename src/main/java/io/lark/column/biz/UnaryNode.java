// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;

import java.util.Objects;

/**
 * A node with one typed input and one typed fetch output.
 *
 * <p>Business code only composes the stream types and implements
 * {@link #compute(ColumnContext, Tensor)}. The framework handles node ids,
 * dependency wiring, output registration and read/write calls.
 */
@SuppressWarnings("this-escape")
public abstract class UnaryNode<I extends ColumnStream<?>, O> extends ColumnNode {

    private final I input;
    private final ColumnStream<O> output;

    protected UnaryNode(I input, Column<O> outputColumn) {
        super(input);
        this.input = Objects.requireNonNull(input, "input");
        this.output = fetch(outputColumn);
    }

    public final I input() {
        return input;
    }

    public final ColumnStream<O> out() {
        return output;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(output, compute(ctx, ctx.read(input)));
    }

    /** Implement the business transformation for the typed input. */
    protected abstract Tensor compute(ColumnContext ctx, Tensor input);
}
