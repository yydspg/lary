// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.columns.Clicks;
import io.lark.examples.recsys.columns.Impressions;
import io.lark.examples.recsys.columns.Orders;
import io.lark.examples.recsys.columns.OverallCtr;
import io.lark.examples.recsys.columns.OverallCvr;

/** Aggregate funnel rates across all rows. */
public final class FunnelSummaryNode extends ColumnNode {

    private final Impressions impressions;
    private final Clicks clicks;
    private final Orders orders;
    private final OverallCtr overallCtr = fetch(OverallCtr.class);
    private final OverallCvr overallCvr = fetch(OverallCvr.class);

    public FunnelSummaryNode(Impressions impressions, Clicks clicks, Orders orders) {
        super(impressions, clicks, orders);
        this.impressions = impressions;
        this.clicks = clicks;
        this.orders = orders;
    }

    public OverallCtr overallCtr() {
        return overallCtr;
    }

    public OverallCvr overallCvr() {
        return overallCvr;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor totalImpressions = Compute.sumTensor(ctx.read(impressions));
        Tensor totalClicks = Compute.sumTensor(ctx.read(clicks));
        Tensor totalOrders = Compute.sumTensor(ctx.read(orders));
        ctx.write(overallCtr, Ratios.divide(totalClicks, totalImpressions));
        ctx.write(overallCvr, Ratios.divide(totalOrders, totalClicks));
    }
}
