// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

import java.util.Objects;

/**
 * A typed single-output node with two inputs.
 *
 * <p>Business code declares the schema through a static
 * {@link NodeSchema} property or an explicit output column and implements
 * {@link #compute(ColumnContext, Tensor, Tensor)}. Inputs may be bound by
 * {@code ColumnModule.node(node, left, right)}.
 */
@SuppressWarnings("this-escape")
public abstract class BinaryNode<A extends ColumnStream<?>,
        B extends ColumnStream<?>, O> extends ModuleNode<O> {

    private A left;
    private B right;

    protected BinaryNode() {
        super();
        bindOutput(NodeSchemas.<O>schemaOf(getClass()).output());
    }

    protected BinaryNode(Column<O> outputColumn) {
        super(outputColumn);
    }

    protected BinaryNode(A left, B right) {
        super();
        bindOutput(NodeSchemas.<O>schemaOf(getClass()).output());
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        bindFeeds(left, right);
    }

    protected BinaryNode(A left, B right, NodeSchema<O> schema) {
        this(left, right, schema.output());
    }

    protected BinaryNode(A left, B right, Column<O> outputColumn) {
        super(outputColumn, left, right);
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
    }

    final void bind(A left, B right) {
        if (this.left != null || this.right != null) {
            throw new IllegalStateException(
                    "BinaryNode " + id() + ": inputs are already bound");
        }
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        bindFeeds(left, right);
    }

    public final A left() {
        return left;
    }

    public final B right() {
        return right;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(out(), compute(ctx, ctx.read(left), ctx.read(right)));
    }

    /** Implement the business transformation for the typed inputs. */
    protected abstract Tensor compute(ColumnContext ctx, Tensor left, Tensor right);
}
