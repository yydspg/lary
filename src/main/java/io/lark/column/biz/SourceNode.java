// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

/** A node with no inputs and one typed fetch output. */
@SuppressWarnings("this-escape")
public abstract class SourceNode<O> extends ColumnNode {

    private final ColumnStream<O> output;

    /** Reads the subclass's public static {@code SCHEMA} property. */
    protected SourceNode() {
        this.output = fetch(NodeSchemas.schemaOf(getClass()));
    }

    protected SourceNode(NodeSchema<O> schema) {
        this.output = fetch(schema);
    }

    protected SourceNode(Column<O> outputColumn) {
        this.output = fetch(outputColumn);
    }

    public final ColumnStream<O> out() {
        return output;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(output, produce(ctx));
    }

    /** Produce the source column. */
    protected abstract Tensor produce(ColumnContext ctx);
}
