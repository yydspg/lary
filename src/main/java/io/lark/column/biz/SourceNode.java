// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

/** A typed single-output node with no inputs. */
@SuppressWarnings("this-escape")
public abstract class SourceNode<O> extends ModuleNode<O> {

    /** Reads the subclass's public static {@code SCHEMA} property. */
    protected SourceNode() {
        super();
        bindOutput(NodeSchemas.<O>schemaOf(getClass()).output());
    }

    protected SourceNode(NodeSchema<O> schema) {
        super(schema.output());
    }

    protected SourceNode(Column<O> outputColumn) {
        super(outputColumn);
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(out(), produce(ctx));
    }

    /** Produce the source column. */
    protected abstract Tensor produce(ColumnContext ctx);
}
