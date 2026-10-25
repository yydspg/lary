// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.schema;

import io.lark.column.DType;
import io.lark.column.biz.Column;

/**
 * The complete recsys column schema.
 *
 * <p>Column definitions are immutable value objects; the graph creates a
 * {@code ColumnStream} handle from a definition when a module declares an input
 * or a node declares an output.
 */
public final class RecSysMetrics {

    // Feed columns.
    public static final Column<Long> IMPRESSIONS =
            new Column<>("impressions", DType.INT64);
    public static final Column<Long> CLICKS =
            new Column<>("clicks", DType.INT64);
    public static final Column<Long> ORDERS =
            new Column<>("orders", DType.INT64);
    public static final Column<Long> GMV_FEN =
            new Column<>("gmv_fen", DType.INT64);
    public static final Column<Long> COUPON_COST_FEN =
            new Column<>("coupon_cost_fen", DType.INT64);
    public static final Column<Long> BUY_AGAIN_ORDERS =
            new Column<>("buy_again_orders", DType.INT64);

    // Per-item metrics.
    public static final Column<Double> CTR =
            new Column<>("ctr", DType.FLOAT64);
    public static final Column<Double> CVR =
            new Column<>("cvr", DType.FLOAT64);
    public static final Column<Double> GMV =
            new Column<>("gmv", DType.FLOAT64);
    public static final Column<Double> COUPON_COST =
            new Column<>("coupon_cost", DType.FLOAT64);
    public static final Column<Double> BUYAGAIN_COST =
            new Column<>("buyagain_cost", DType.FLOAT64);
    public static final Column<Double> COUPON_ROI =
            new Column<>("coupon_roi", DType.FLOAT64);
    public static final Column<Double> WEIGHT =
            new Column<>("weight", DType.FLOAT64);

    // Portfolio summary.
    public static final Column<Double> TOTAL_GMV =
            new Column<>("total_gmv", DType.FLOAT64);
    public static final Column<Double> TOTAL_COUPON_COST =
            new Column<>("total_coupon_cost", DType.FLOAT64);
    public static final Column<Double> OVERALL_CTR =
            new Column<>("overall_ctr", DType.FLOAT64);
    public static final Column<Double> OVERALL_CVR =
            new Column<>("overall_cvr", DType.FLOAT64);
    public static final Column<Double> OVERALL_ROI =
            new Column<>("overall_roi", DType.FLOAT64);
    public static final Column<Double> OVERALL_BUYAGAIN_COST =
            new Column<>("overall_buyagain_cost", DType.FLOAT64);
    public static final Column<Double> AVERAGE_WEIGHT =
            new Column<>("average_weight", DType.FLOAT64);

    private RecSysMetrics() {
    }
}
