// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.AmountFen;
import io.lark.examples.risk.columns.AmountYuan;

/** Convert integer fen to a floating-point yuan amount. */
public final class AmountToYuanNode extends UnaryNode<AmountFen, AmountYuan> {

    public AmountToYuanNode(AmountFen input) {
        super(input, AmountYuan.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor amountFen) {
        return Compute.divScalar(amountFen, 100.0);
    }
}
