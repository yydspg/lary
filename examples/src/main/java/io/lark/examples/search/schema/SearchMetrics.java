// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.schema;

import io.lark.column.tensor.DType;
import io.lark.column.biz.Column;

/** Typed schema for the search/retargeting sample. */
public final class SearchMetrics {

    private SearchMetrics() {
    }

    // Raw search inputs.
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
    public static final Column<Long> DELAYED_ORDERS =
            new Column<>("delayed_orders", DType.INT64);
    public static final Column<Long> DELAYED_GMV_FEN =
            new Column<>("delayed_gmv_fen", DType.INT64);
    public static final Column<Long> RETARGET_CLICKS =
            new Column<>("retarget_clicks", DType.INT64);
    public static final Column<Long> RETARGET_COST_FEN =
            new Column<>("retarget_cost_fen", DType.INT64);

    // Computed metrics.
    public static final Column<Double> CTR =
            new Column<>("ctr", DType.FLOAT64);
    public static final Column<Double> CVR =
            new Column<>("cvr", DType.FLOAT64);
    public static final Column<Double> CTCVR =
            new Column<>("ctcvr", DType.FLOAT64);
    public static final Column<Double> REBUY_SAME_LOSS =
            new Column<>("rebuy_same_loss", DType.FLOAT64);
    public static final Column<Double> BUYAGAIN =
            new Column<>("buyagain", DType.FLOAT64);
    public static final Column<Double> COUPON_ROI =
            new Column<>("couponroi", DType.FLOAT64);
    public static final Column<Double> RETARGET_AD =
            new Column<>("retargetAd", DType.FLOAT64);
    public static final Column<Double> GMV =
            new Column<>("gmv", DType.FLOAT64);
    public static final Column<Double> DELAY =
            new Column<>("delay", DType.FLOAT64);

    // Internal derived columns.
    public static final Column<Double> COUPON_COST =
            new Column<>("coupon_cost", DType.FLOAT64);
}
