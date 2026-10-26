// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.data.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Coupon spend per repeat order. */
public final class BuyAgainCostNode
        extends BinaryNode<ColumnStream<Double>, ColumnStream<Long>, Double> {

    public static final NodeSchema<Double> SCHEMA =
            NodeSchema.of(RecSysMetrics.BUYAGAIN_COST);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor cost, Tensor repeatOrders) {
        return Ratios.divide(cost, repeatOrders);
    }
}
