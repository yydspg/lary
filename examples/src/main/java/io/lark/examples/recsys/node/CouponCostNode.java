// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.columns.CouponCost;
import io.lark.examples.recsys.columns.CouponCostFen;

/** Fen to yuan for coupon spend. */
public final class CouponCostNode extends UnaryNode<CouponCostFen, CouponCost> {

    public CouponCostNode(CouponCostFen input) {
        super(input, CouponCost.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor costFen) {
        return Compute.divScalar(costFen, 100.0);
    }
}
