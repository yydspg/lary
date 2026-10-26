// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.data.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.Column;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;

/** Element-wise ratio of two typed streams. */
public final class RatioNode extends BinaryNode<ColumnStream<?>, ColumnStream<?>, Double> {

    public RatioNode(Column<Double> output) {
        super(output);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor left, Tensor right) {
        return Ratios.divide(left, right);
    }
}
