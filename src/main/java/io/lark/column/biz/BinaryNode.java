// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;

import java.util.Objects;

/**
 * A node with two typed inputs and one typed fetch output.
 *
 * <p>The no-arg constructor reads the subclass's static {@link NodeSchema}
 * {@code SCHEMA} property; inputs are bound by
 * {@code ColumnModule.node(node, left, right)}. The explicit input /
 * {@code outputColumn} constructors remain for custom nodes and dynamic
 * schemas.
 */
@SuppressWarnings("this-escape")
public abstract class BinaryNode<A extends ColumnStream<?>,
        B extends ColumnStream<?>, O> extends ColumnNode {

    private A left;
    private B right;
    private final ColumnStream<O> output;

    /**
     * No-arg form for module wiring. Inputs are bound by
     * {@code ColumnModule.node(node, left, right)}; {@code SCHEMA} comes from
     * the subclass.
     */
    protected BinaryNode() {
        super();
        this.output = fetch(NodeSchemas.schemaOf(getClass()));
    }

    /** No-arg wiring form with an explicit output definition. */
    protected BinaryNode(Column<O> outputColumn) {
        super();
        this.output = fetch(outputColumn);
    }

    /** Reads the subclass's public static {@code SCHEMA} property. */
    protected BinaryNode(A left, B right) {
        super(left, right);
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        this.output = fetch(NodeSchemas.schemaOf(getClass()));
    }

    protected BinaryNode(A left, B right, NodeSchema<O> schema) {
        this(left, right, schema.output());
    }

    protected BinaryNode(A left, B right, Column<O> outputColumn) {
        super(left, right);
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
        this.output = fetch(outputColumn);
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

    public final ColumnStream<O> out() {
        return output;
    }

    @Override
    public final void compute(ColumnContext ctx) {
        ctx.write(output, compute(ctx, ctx.read(left), ctx.read(right)));
    }

    /** Implement the business transformation for the typed inputs. */
    protected abstract Tensor compute(ColumnContext ctx, Tensor left, Tensor right);
}
