// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.risk.columns.TxnCount24h;
import io.lark.examples.risk.columns.TxnVelocity;

/** Daily transaction count to transactions per hour. */
public final class TxnVelocityNode
        extends UnaryNode<TxnCount24h, TxnVelocity> {

    public TxnVelocityNode(TxnCount24h input) {
        super(input, TxnVelocity.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor count) {
        return Compute.divScalar(count, 24.0);
    }
}
