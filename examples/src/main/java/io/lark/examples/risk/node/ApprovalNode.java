// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.Approved;
import io.lark.examples.risk.columns.RiskScore;

/** Approves transactions below the high-risk threshold. */
public final class ApprovalNode extends UnaryNode<RiskScore, Approved> {

    private static final double APPROVAL_LIMIT = 1_000.0;

    public ApprovalNode(RiskScore score) {
        super(score, Approved.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor score) {
        return Compute.ltScalar(score, APPROVAL_LIMIT);
    }
}
