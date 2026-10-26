// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.Tensor;
import io.lark.column.biz.Column;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;

/** Computes {@code 1 - input}, used for the same-item rebuy loss rate. */
public final class OneMinusNode extends UnaryNode<ColumnStream<?>, Double> {

    public OneMinusNode(Column<Double> output) {
        super(output);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor input) {
        return Compute.addScalar(Compute.neg(input), 1.0);
    }
}
