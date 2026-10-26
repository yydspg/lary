// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.data.Tensor;
import io.lark.column.biz.Column;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;

/** Divides a numeric stream by a scalar (fen -> yuan, for example). */
public final class ScaleNode extends UnaryNode<ColumnStream<?>, Double> {

    private final double divisor;

    public ScaleNode(Column<Double> output, double divisor) {
        super(output);
        if (divisor == 0.0) {
            throw new IllegalArgumentException("divisor must not be zero");
        }
        this.divisor = divisor;
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor input) {
        return Compute.divScalar(input, divisor);
    }
}
