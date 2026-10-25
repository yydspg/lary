// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;

/** A node with no inputs and one typed fetch output. */
@SuppressWarnings("this-escape")
public abstract class SourceNode<O extends ColumnStream<?>> extends ColumnNode {

    private final O output;

    protected SourceNode(Class<O> outputType) {
        this.output = fetch(outputType);
    }

    public final O out() {
        return output;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(output, produce(ctx));
    }

    /** Produce the source column. */
    protected abstract Tensor produce(ColumnContext ctx);
}
