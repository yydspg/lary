// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.module;

import io.lark.column.biz.ColumnModule;
import io.lark.examples.risk.node.ApprovalNode;
import io.lark.examples.risk.node.RiskBandNode;
import io.lark.examples.risk.node.RiskScoreNode;

/** Scores transactions and derives risk bands / approval flags. */
public final class RiskScoreModule extends ColumnModule {

    private final RiskScoreNode score;
    private final RiskBandNode band;
    private final ApprovalNode approval;

    public RiskScoreModule(TransactionFeatureModule features) {
        score = add(new RiskScoreNode(
                features.weighted().out(),
                features.velocity().out(),
                features.chargebackRatio().out(),
                features.merchantTier().out()));
        band = add(new RiskBandNode(score.out()));
        approval = add(new ApprovalNode(score.out()));
        dependsOn(TransactionFeatureModule.class);
        output(score.out(), band.level(), band.highMask(), approval.out());
    }

    public RiskScoreNode score() {
        return score;
    }

    public RiskBandNode band() {
        return band;
    }

    public ApprovalNode approval() {
        return approval;
    }
}
