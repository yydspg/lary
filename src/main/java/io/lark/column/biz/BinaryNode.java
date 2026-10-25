// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;

import java.util.Objects;

/** A node with two typed inputs and one typed fetch output. */
@SuppressWarnings("this-escape")
public abstract class BinaryNode<A extends ColumnStream<?>,
        B extends ColumnStream<?>, O extends ColumnStream<?>>
        extends ColumnNode {

    private final A left;
    private final B right;
    private final O output;

    protected BinaryNode(A left, B right, Class<O> outputType) {
        super(left, right);
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        this.output = fetch(outputType);
    }

    public final A left() {
        return left;
    }

    public final B right() {
        return right;
    }

    public final O out() {
        return output;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(output, compute(ctx, ctx.read(left), ctx.read(right)));
    }

    /** Implement the business transformation for the typed inputs. */
    protected abstract Tensor compute(ColumnContext ctx, Tensor left, Tensor right);
}
