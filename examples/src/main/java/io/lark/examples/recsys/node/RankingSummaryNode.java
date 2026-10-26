// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.data.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Average ranking weight across all rows. */
public final class RankingSummaryNode
        extends UnaryNode<ColumnStream<Double>, Double> {

    public static final NodeSchema<Double> SCHEMA =
            NodeSchema.of(RecSysMetrics.AVERAGE_WEIGHT);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor weight) {
        return Compute.meanTensor(weight);
    }
}
