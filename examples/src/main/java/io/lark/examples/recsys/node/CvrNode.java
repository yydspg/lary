// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.examples.recsys.columns.Clicks;
import io.lark.examples.recsys.columns.Cvr;
import io.lark.examples.recsys.columns.Orders;

/** Conversion rate: orders / clicks. */
public final class CvrNode extends BinaryNode<Orders, Clicks, Cvr> {

    public CvrNode(Orders orders, Clicks clicks) {
        super(orders, clicks, Cvr.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor orders, Tensor clicks) {
        return Ratios.divide(orders, clicks);
    }
}
