// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.examples.recsys.columns.BuyAgainCost;
import io.lark.examples.recsys.columns.BuyAgainOrders;
import io.lark.examples.recsys.columns.CouponCost;

/** Coupon spend per repeat order. */
public final class BuyAgainCostNode
        extends BinaryNode<CouponCost, BuyAgainOrders, BuyAgainCost> {

    public BuyAgainCostNode(CouponCost cost, BuyAgainOrders repeatOrders) {
        super(cost, repeatOrders, BuyAgainCost.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor cost, Tensor repeatOrders) {
        return Ratios.divide(cost, repeatOrders);
    }
}
