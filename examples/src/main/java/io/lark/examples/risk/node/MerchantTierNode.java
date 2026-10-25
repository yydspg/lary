// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.node;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.examples.risk.columns.MerchantId;
import io.lark.examples.risk.columns.MerchantTier;

/**
 * Simulates an external merchant-enrichment service.
 *
 * <p>The service is intentionally unavailable in this demo; the fallback
 * publishes a conservative tier so the rest of the risk pipeline still runs.
 */
public final class MerchantTierNode
        extends UnaryNode<MerchantId, MerchantTier> {

    public MerchantTierNode(MerchantId input) {
        super(input, MerchantTier.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor merchantIds) {
        throw new IllegalStateException("merchant enrichment service unavailable");
    }

    @Override
    public boolean fallback(ColumnContext ctx, Throwable error) {
        int rows = ctx.read(input()).size();
        ctx.write(out(), Tensor.full(rows, 1.0, DType.INT64));
        return true;
    }
}
