// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Coupon ROI: GMV / coupon spend. */
public final class CouponRoiNode
        extends BinaryNode<ColumnStream<Double>, ColumnStream<Double>, Double> {

    public CouponRoiNode(ColumnStream<Double> gmv, ColumnStream<Double> cost) {
        super(gmv, cost, RecSysMetrics.COUPON_ROI);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor gmv, Tensor cost) {
        return Ratios.divide(gmv, cost);
    }
}
