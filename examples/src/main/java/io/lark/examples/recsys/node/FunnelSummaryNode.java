// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnStream;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Aggregate funnel rates across all rows. */
public final class FunnelSummaryNode extends ColumnNode {

    private final ColumnStream<Long> impressions;
    private final ColumnStream<Long> clicks;
    private final ColumnStream<Long> orders;
    private final ColumnStream<Double> overallCtr =
            fetch(RecSysMetrics.OVERALL_CTR);
    private final ColumnStream<Double> overallCvr =
            fetch(RecSysMetrics.OVERALL_CVR);

    public FunnelSummaryNode(ColumnStream<Long> impressions,
                             ColumnStream<Long> clicks,
                             ColumnStream<Long> orders) {
        super(impressions, clicks, orders);
        this.impressions = impressions;
        this.clicks = clicks;
        this.orders = orders;
    }

    public ColumnStream<Double> overallCtr() {
        return overallCtr;
    }

    public ColumnStream<Double> overallCvr() {
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
