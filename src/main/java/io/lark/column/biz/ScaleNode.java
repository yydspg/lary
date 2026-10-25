// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

/**
 * Business transformation: multiply a typed stream by a weight.
 *
 * <pre>{@code
 * ScaleNode<WeightedAmount> weighted =
 *         new ScaleNode<>(amount.out(), WeightedAmount.class, 0.4);
 * }</pre>
 */
public final class ScaleNode<O extends ColumnStream<?>>
        extends UnaryNode<ColumnStream<?>, O> {

    private final double weight;

    public ScaleNode(ColumnStream<?> input, Class<O> outputType, double weight) {
        super(input, outputType);
        this.weight = weight;
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor input) {
        return Compute.mulScalar(input, weight);
    }
}
