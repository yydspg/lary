// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.columns.AverageWeight;
import io.lark.examples.recsys.columns.Weight;

/** Average ranking weight across all rows. */
public final class RankingSummaryNode
        extends UnaryNode<Weight, AverageWeight> {

    public RankingSummaryNode(Weight weight) {
        super(weight, AverageWeight.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor weight) {
        return Compute.meanTensor(weight);
    }
}
