// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

import java.util.Objects;

/**
 * A node with one typed input and one typed fetch output.
 *
 * <p>Business code only declares the node schema with a static
 * {@link NodeSchema} property and implements
 * {@link #compute(ColumnContext, Tensor)}. The input is bound by
 * {@code ColumnModule.node(node, input)}; the framework handles node ids,
 * dependency wiring, output registration and read/write calls.
 */
@SuppressWarnings("this-escape")
public abstract class UnaryNode<I extends ColumnStream<?>, O> extends ColumnNode {

    private I input;
    private final ColumnStream<O> output;

    /**
     * No-arg form for module wiring. The input is bound by
     * {@code ColumnModule.node(node, input)}; {@code SCHEMA} comes from the
     * subclass.
     */
    protected UnaryNode() {
        super();
        this.output = fetch(NodeSchemas.schemaOf(getClass()));
    }

    /** No-arg wiring form with an explicit output definition. */
    protected UnaryNode(Column<O> outputColumn) {
        super();
        this.output = fetch(outputColumn);
    }

    /** Reads the subclass's public static {@code SCHEMA} property. */
    protected UnaryNode(I input) {
        super(input);
        this.input = Objects.requireNonNull(input, "input");
        this.output = fetch(NodeSchemas.schemaOf(getClass()));
    }

    protected UnaryNode(I input, NodeSchema<O> schema) {
        this(input, schema.output());
    }

    protected UnaryNode(I input, Column<O> outputColumn) {
        super(input);
        this.input = Objects.requireNonNull(input, "input");
        this.output = fetch(outputColumn);
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
