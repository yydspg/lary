// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.tensor.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Click-through rate: clicks / impressions. */
public final class CtrNode
        extends BinaryNode<ColumnStream<Long>, ColumnStream<Long>, Double> {

    public static final NodeSchema<Double> SCHEMA =
            NodeSchema.of(RecSysMetrics.CTR);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor clicks, Tensor impressions) {
        return Ratios.divide(clicks, impressions);
    }
}
