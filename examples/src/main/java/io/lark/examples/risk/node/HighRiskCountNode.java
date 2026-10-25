// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.HighMask;
import io.lark.examples.risk.columns.HighRiskCount;

/** Counts high-risk transactions. */
public final class HighRiskCountNode
        extends UnaryNode<HighMask, HighRiskCount> {

    public HighRiskCountNode(HighMask highMask) {
        super(highMask, HighRiskCount.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor highMask) {
        return Compute.sumTensor(highMask);
    }
}
