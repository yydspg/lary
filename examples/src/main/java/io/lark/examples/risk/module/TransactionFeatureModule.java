// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ScaleNode;
import io.lark.examples.risk.columns.AmountFen;
import io.lark.examples.risk.columns.ChargebackCount;
import io.lark.examples.risk.columns.MerchantId;
import io.lark.examples.risk.columns.TxnCount24h;
import io.lark.examples.risk.columns.UserId;
import io.lark.examples.risk.columns.WeightedAmount;
import io.lark.examples.risk.node.AmountToYuanNode;
import io.lark.examples.risk.node.ChargebackRatioNode;
import io.lark.examples.risk.node.MerchantTierNode;
import io.lark.examples.risk.node.TxnVelocityNode;

/** Raw transaction columns to the features consumed by risk scoring. */
public final class TransactionFeatureModule extends ColumnModule {

    private final UserId userId = input(UserId.class);
    private final AmountFen amountFen = input(AmountFen.class);
    private final TxnCount24h txnCount = input(TxnCount24h.class);
    private final ChargebackCount chargebackCount = input(ChargebackCount.class);
    private final MerchantId merchantId = input(MerchantId.class);

    private final AmountToYuanNode amount = add(new AmountToYuanNode(amountFen));
    private final TxnVelocityNode velocity = add(new TxnVelocityNode(txnCount));
    private final ChargebackRatioNode chargebackRatio =
            add(new ChargebackRatioNode(chargebackCount, txnCount));
    private final ScaleNode<WeightedAmount> weighted =
            add(new ScaleNode<>(amount.out(), WeightedAmount.class, 0.4));
    private final MerchantTierNode merchantTier =
            add(new MerchantTierNode(merchantId));

    public TransactionFeatureModule() {
        output(userId, amount.out(), velocity.out(), chargebackRatio.out(),
                weighted.out(), merchantTier.out());
    }

    public UserId userId() {
        return userId;
    }

    public AmountFen amountFen() {
        return amountFen;
    }

    public TxnCount24h txnCount() {
        return txnCount;
    }

    public ChargebackCount chargebackCount() {
        return chargebackCount;
    }

    public MerchantId merchantId() {
        return merchantId;
    }

    public AmountToYuanNode amount() {
        return amount;
    }

    public TxnVelocityNode velocity() {
        return velocity;
    }

    public ChargebackRatioNode chargebackRatio() {
        return chargebackRatio;
    }

    public ScaleNode<WeightedAmount> weighted() {
        return weighted;
    }

    public MerchantTierNode merchantTier() {
        return merchantTier;
    }
}
