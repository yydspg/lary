// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.ChargebackCount;
import io.lark.examples.risk.columns.ChargebackRatio;
import io.lark.examples.risk.columns.TxnCount24h;

/** Chargebacks divided by transaction count. */
public final class ChargebackRatioNode
        extends BinaryNode<ChargebackCount, TxnCount24h, ChargebackRatio> {

    public ChargebackRatioNode(ChargebackCount chargebacks, TxnCount24h transactions) {
        super(chargebacks, transactions, ChargebackRatio.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor chargebacks,
                             Tensor transactions) {
        return Compute.div(chargebacks, transactions);
    }
}
