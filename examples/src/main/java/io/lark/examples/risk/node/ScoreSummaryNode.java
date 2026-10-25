// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.MaxRiskScore;
import io.lark.examples.risk.columns.MeanRiskScore;
import io.lark.examples.risk.columns.RiskScore;
import io.lark.examples.risk.columns.TotalRiskScore;

/** Portfolio-level risk aggregation. */
public final class ScoreSummaryNode extends ColumnNode {

    private final RiskScore score;
    private final TotalRiskScore total = fetch(TotalRiskScore.class);
    private final MeanRiskScore mean = fetch(MeanRiskScore.class);
    private final MaxRiskScore max = fetch(MaxRiskScore.class);

    public ScoreSummaryNode(RiskScore score) {
        super(score);
        this.score = score;
    }

    public TotalRiskScore total() {
        return total;
    }

    public MeanRiskScore mean() {
        return mean;
    }

    public MaxRiskScore max() {
        return max;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor scores = ctx.read(score);
        ctx.write(total, Compute.sumTensor(scores));
        ctx.write(mean, Compute.meanTensor(scores));
        ctx.write(max, Compute.maxTensor(scores));
    }
}
