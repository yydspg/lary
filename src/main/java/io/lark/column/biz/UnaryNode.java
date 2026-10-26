// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

import java.util.Objects;

/**
 * A typed single-output node with one input.
 *
 * <p>Business code declares the schema through a static
 * {@link NodeSchema} property or an explicit output column and implements
 * {@link #compute(ColumnContext, Tensor)}. The input may be bound by
 * {@code ColumnModule.node(node, input)}.
 */
@SuppressWarnings("this-escape")
public abstract class UnaryNode<I extends ColumnStream<?>, O>
        extends ModuleNode<O> {

    private I input;

    protected UnaryNode() {
        super();
        bindOutput(NodeSchemas.<O>schemaOf(getClass()).output());
    }

    protected UnaryNode(Column<O> outputColumn) {
        super(outputColumn);
    }

    protected UnaryNode(I input) {
        super();
        bindOutput(NodeSchemas.<O>schemaOf(getClass()).output());
        this.input = Objects.requireNonNull(input, "input");
        bindFeeds(input);
    }

    protected UnaryNode(I input, NodeSchema<O> schema) {
        this(input, schema.output());
    }

    protected UnaryNode(I input, Column<O> outputColumn) {
        super(outputColumn, input);
        this.input = Objects.requireNonNull(input, "input");
    }

    final void bind(I input) {
        if (this.input != null) {
            throw new IllegalStateException(
                    "UnaryNode " + id() + ": input is already bound");
        }
        this.input = Objects.requireNonNull(input, "input");
        bindFeeds(input);
    }

    public final I input() {
        return input;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(out(), compute(ctx, ctx.read(input)));
    }

    /** Implement the business transformation for the typed input. */
    protected abstract Tensor compute(ColumnContext ctx, Tensor input);
}
