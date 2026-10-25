// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

/**
 * Business transformation: apply a fixed unit-conversion factor.
 *
 * <pre>{@code
 * UnitConvertNode<AmountLi> li =
 *         new UnitConvertNode<>(amount.out(), AmountLi.class, 10.0);
 * }</pre>
 */
public final class UnitConvertNode<O extends ColumnStream<?>>
        extends UnaryNode<ColumnStream<?>, O> {

    private final double factor;

    public UnitConvertNode(ColumnStream<?> input, Class<O> outputType,
                           double factor) {
        super(input, outputType);
        this.factor = factor;
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor input) {
        return Compute.mulScalar(input, factor);
    }
}
