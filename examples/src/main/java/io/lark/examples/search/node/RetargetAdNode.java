// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.data.Tensor;
import io.lark.column.data.TensorShape;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.schema.SearchMetrics;

/**
 * Produces a 2-D tensor: one row per input row and three retargeting metrics
 * {@code [click_share, cost_share, roi]}.
 */
public final class RetargetAdNode extends ColumnNode {

    private final ColumnStream<Long> retargetClicks;
    private final ColumnStream<Long> clicks;
    private final ColumnStream<Long> retargetCostFen;
    private final ColumnStream<Long> gmvFen;
    private final ColumnStream<Double> out = fetch(SearchMetrics.RETARGET_AD);

    public RetargetAdNode(ColumnStream<Long> retargetClicks,
                          ColumnStream<Long> clicks,
                          ColumnStream<Long> retargetCostFen,
                          ColumnStream<Long> gmvFen) {
        super(retargetClicks, clicks, retargetCostFen, gmvFen);
        this.retargetClicks = retargetClicks;
        this.clicks = clicks;
        this.retargetCostFen = retargetCostFen;
        this.gmvFen = gmvFen;
    }

    public ColumnStream<Double> out() {
        return out;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor clickShare = Ratios.divide(ctx.read(retargetClicks), ctx.read(clicks));
        Tensor costShare = Ratios.divide(ctx.read(retargetCostFen), ctx.read(gmvFen));
        Tensor roi = Ratios.divide(ctx.read(gmvFen), ctx.read(retargetCostFen));

        int rows = clickShare.size();
        double[] click = clickShare.doubleData();
        double[] cost = costShare.doubleData();
        double[] returnOnAd = roi.doubleData();
        double[] values = new double[rows * 3];
        for (int i = 0; i < rows; i++) {
            values[i * 3] = click[i];
            values[i * 3 + 1] = cost[i];
            values[i * 3 + 2] = returnOnAd[i];
        }
        ctx.write(out, Tensor.fromDoubles(TensorShape.of(rows, 3), values));
    }
}
