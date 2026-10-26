// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.tensor.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Coupon ROI: GMV / coupon spend. */
public final class CouponRoiNode
        extends BinaryNode<ColumnStream<Double>, ColumnStream<Double>, Double> {

    public static final NodeSchema<Double> SCHEMA = NodeSchema.of(RecSysMetrics.COUPON_ROI);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor gmv, Tensor cost) {
        return Ratios.divide(gmv, cost);
    }
}
