// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.HighMask;
import io.lark.examples.risk.columns.RiskLevel;
import io.lark.examples.risk.columns.RiskScore;

/** Converts a continuous risk score into a level and a high-risk mask. */
public final class RiskBandNode extends ColumnNode {

    private static final double HIGH_THRESHOLD = 1_000.0;
    private static final double MEDIUM_THRESHOLD = 200.0;

    private final RiskScore score;
    private final RiskLevel level = fetch(RiskLevel.class);
    private final HighMask highMask = fetch(HighMask.class);

    public RiskBandNode(RiskScore score) {
        super(score);
        this.score = score;
    }

    public RiskLevel level() {
        return level;
    }

    public HighMask highMask() {
        return highMask;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor scores = ctx.read(score);
        int rows = scores.size();
        Tensor high = Compute.gtScalar(scores, HIGH_THRESHOLD);
        Tensor medium = Compute.gtScalar(scores, MEDIUM_THRESHOLD);

        Tensor lowValue = Tensor.full(rows, 0.0, DType.INT64);
        Tensor mediumValue = Tensor.full(rows, 1.0, DType.INT64);
        Tensor highValue = Tensor.full(rows, 2.0, DType.INT64);
        Tensor levelValue = Compute.select(high, highValue,
                Compute.select(medium, mediumValue, lowValue));

        ctx.write(highMask, high);
        ctx.write(level, levelValue);
    }
}
