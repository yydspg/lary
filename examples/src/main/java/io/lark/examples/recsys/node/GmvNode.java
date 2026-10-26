// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.tensor.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Fen to yuan for gross merchandise value. */
public final class GmvNode extends UnaryNode<ColumnStream<Long>, Double> {

    public static final NodeSchema<Double> SCHEMA =
            NodeSchema.of(RecSysMetrics.GMV);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor gmvFen) {
        return Compute.divScalar(gmvFen, 100.0);
    }
}
