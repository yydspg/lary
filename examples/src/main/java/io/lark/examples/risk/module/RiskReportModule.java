// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.module;

import io.lark.column.biz.ColumnModule;
import io.lark.examples.risk.node.HighRiskCountNode;
import io.lark.examples.risk.node.ScoreSummaryNode;

/** Portfolio-level risk report. */
public final class RiskReportModule extends ColumnModule {

    private final ScoreSummaryNode summary;
    private final HighRiskCountNode highRisk;

    public RiskReportModule(RiskScoreModule risk) {
        summary = add(new ScoreSummaryNode(risk.score().out()));
        highRisk = add(new HighRiskCountNode(risk.band().highMask()));
        dependsOn(RiskScoreModule.class);
        output(summary.total(), summary.mean(), summary.max(), highRisk.out());
    }

    public ScoreSummaryNode summary() {
        return summary;
    }

    public HighRiskCountNode highRisk() {
        return highRisk;
    }
}
