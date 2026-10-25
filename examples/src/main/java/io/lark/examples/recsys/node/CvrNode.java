// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Conversion rate: orders / clicks. */
public final class CvrNode
        extends BinaryNode<ColumnStream<Long>, ColumnStream<Long>, Double> {

    public static final NodeSchema<Double> SCHEMA =
            NodeSchema.of(RecSysMetrics.CVR);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor orders, Tensor clicks) {
        return Ratios.divide(orders, clicks);
    }
}
