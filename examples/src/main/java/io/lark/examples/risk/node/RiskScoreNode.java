// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.ChargebackRatio;
import io.lark.examples.risk.columns.MerchantTier;
import io.lark.examples.risk.columns.RiskScore;
import io.lark.examples.risk.columns.TxnVelocity;
import io.lark.examples.risk.columns.WeightedAmount;

/**
 * Risk score = weighted amount + velocity component + chargeback component +
 * merchant-tier component.
 */
public final class RiskScoreNode extends ColumnNode {

    private final WeightedAmount weightedAmount;
    private final TxnVelocity velocity;
    private final ChargebackRatio chargebackRatio;
    private final MerchantTier merchantTier;
    private final RiskScore out = fetch(RiskScore.class);

    public RiskScoreNode(WeightedAmount weightedAmount, TxnVelocity velocity,
                         ChargebackRatio chargebackRatio,
                         MerchantTier merchantTier) {
        super(weightedAmount, velocity, chargebackRatio, merchantTier);
        this.weightedAmount = weightedAmount;
        this.velocity = velocity;
        this.chargebackRatio = chargebackRatio;
        this.merchantTier = merchantTier;
    }

    public RiskScore out() {
        return out;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor amount = ctx.read(weightedAmount);
        Tensor velocityPart = Compute.mulScalar(ctx.read(velocity), 10.0);
        Tensor chargebackPart = Compute.mulScalar(ctx.read(chargebackRatio), 100.0);
        Tensor tierPart = Compute.mulScalar(ctx.read(merchantTier), 5.0);

        Tensor score = Compute.add(
                Compute.add(amount, velocityPart),
                Compute.add(chargebackPart, tierPart));
        ctx.write(out, score);
    }
}
