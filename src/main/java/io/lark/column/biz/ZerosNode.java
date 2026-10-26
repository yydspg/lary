// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;

import java.util.Objects;

/**
 * Source node that produces a zero-filled tensor with an explicit shape.
 *
 * <pre>{@code
 * ColumnStream<Double> zeros =
 *         node(new ZerosNode<>(FEATURE, TensorShape.of(rows, 3))).out();
 * }</pre>
 */
public final class ZerosNode<O> extends SourceNode<O> {

    private final TensorShape shape;

    public ZerosNode(Column<O> output, TensorShape shape) {
        super(output);
        this.shape = Objects.requireNonNull(shape, "shape");
    }

    public ZerosNode(NodeSchema<O> schema, TensorShape shape) {
        super(schema);
        this.shape = Objects.requireNonNull(shape, "shape");
    }

    public TensorShape shape() {
        return shape;
    }

    @Override
    protected Tensor produce(ColumnContext ctx) {
        return Tensor.zeros(shape, out().dtype());
    }
}
