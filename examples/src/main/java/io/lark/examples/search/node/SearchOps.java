// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.biz.Column;
import io.lark.column.biz.ColumnOp;
import io.lark.column.biz.ColumnOps;
import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;
import io.lark.examples.search.schema.SearchMetrics;

/** Business-facing ops used by the search modules. */
public final class SearchOps {

    private SearchOps() {
    }

    public static ColumnOp<Double> ratio(Column<Double> output) {
        return ColumnOps.ratio(output);
    }

    public static ColumnOp<Double> mul(Column<Double> output) {
        return ColumnOps.mul(output);
    }

    public static ColumnOp<Double> oneMinus(Column<Double> output) {
        return ColumnOps.oneMinus(output);
    }

    public static ColumnOp<Double> scale(Column<Double> output, double divisor) {
        return ColumnOps.scale(output, divisor);
    }

    /** 2-D retargeting tensor: [click_share, cost_share, roi]. */
    public static ColumnOp<Double> retargetAd() {
        return new ColumnOp<>(SearchMetrics.RETARGET_AD, ctx -> {
            Tensor clickShare = Ratios.divide(ctx.input(0), ctx.input(1));
            Tensor costShare = Ratios.divide(ctx.input(2), ctx.input(3));
            Tensor roi = Ratios.divide(ctx.input(3), ctx.input(2));

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
            ctx.output(Tensor.fromDoubles(TensorShape.of(rows, 3), values));
        });
    }

    /** 3-D delay tensor: [rows, 2, 2] with [row, orders/GMV, on-time/delayed]. */
    public static ColumnOp<Double> delay() {
        return new ColumnOp<>(SearchMetrics.DELAY, ctx -> {
            Tensor orderDelay = Ratios.divide(ctx.input(0), ctx.input(1));
            Tensor gmvDelay = Ratios.divide(ctx.input(2), ctx.input(3));

            int rows = orderDelay.size();
            double[] order = orderDelay.doubleData();
            double[] gmv = gmvDelay.doubleData();
            double[] values = new double[rows * 4];
            for (int i = 0; i < rows; i++) {
                values[i * 4] = 1.0 - order[i];
                values[i * 4 + 1] = order[i];
                values[i * 4 + 2] = 1.0 - gmv[i];
                values[i * 4 + 3] = gmv[i];
            }
            ctx.output(Tensor.fromDoubles(TensorShape.of(rows, 2, 2), values));
        });
    }
}
