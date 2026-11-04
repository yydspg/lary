// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.biz.Column;
import io.lark.column.tensor.DType;

/**
 * Shared search schema: every module references the same {@link Column}
 * definitions instead of declaring private names.
 *
 * <p>The enum carries the name and dtype; typed accessors keep module wiring
 * readable:
 *
 * <pre>{@code
 * pvId = feed(PV_ID.longColumn(), SearchRow::pvId);
 * ctr = node(CTR.doubleColumn(), RatioOp.class, clicks, impressions);
 * }</pre>
 */
public enum SearchSchema {

    // Request / raw inputs.
    PV_ID("pv_id", DType.INT64),
    GOODS_ID("goods_id", DType.INT64),
    IMPRESSIONS("impressions", DType.INT64),
    CLICKS("clicks", DType.INT64),
    ORDERS("orders", DType.INT64),
    GMV_FEN("gmv_fen", DType.INT64),
    COUPON_COST_FEN("coupon_cost_fen", DType.INT64),
    BUY_AGAIN_ORDERS("buy_again_orders", DType.INT64),
    DELAYED_ORDERS("delayed_orders", DType.INT64),
    DELAYED_GMV_FEN("delayed_gmv_fen", DType.INT64),
    RETARGET_CLICKS("retarget_clicks", DType.INT64),
    RETARGET_COST_FEN("retarget_cost_fen", DType.INT64),
    PERTURBATION("perturbation", DType.FLOAT64),
    ORIGINAL_SCORE("original_score", DType.FLOAT64),

    // Computed metrics and ranking outputs.
    CTR("ctr", DType.FLOAT64),
    CVR("cvr", DType.FLOAT64),
    CTCVR("ctcvr", DType.FLOAT64),
    GMV("gmv", DType.FLOAT64),
    BUYAGAIN("buyagain", DType.FLOAT64),
    REBUY_SAME_LOSS("rebuy_same_loss", DType.FLOAT64),
    COUPON_ROI("couponroi", DType.FLOAT64),
    RETARGET_AD("retargetAd", DType.FLOAT64),
    DELAY("delay", DType.FLOAT64),
    RANKING_SCORE("ranking_score", DType.FLOAT64),
    ADJUSTED_SCORE("adjusted_score", DType.FLOAT64),
    SELECTED("selected", DType.INT64);

    private final Column<?> column;

    SearchSchema(String name, DType dtype) {
        this.column = new Column<>(name, dtype);
    }

    public String columnName() {
        return column.name();
    }

    @SuppressWarnings("unchecked")
    public Column<Long> longColumn() {
        if (column.dtype() != DType.INT64) {
            throw new IllegalStateException(
                    column.name() + " is " + column.dtype()
                            + ", not INT64");
        }
        return (Column<Long>) column;
    }

    @SuppressWarnings("unchecked")
    public Column<Double> doubleColumn() {
        if (column.dtype() != DType.FLOAT64) {
            throw new IllegalStateException(
                    column.name() + " is " + column.dtype()
                            + ", not FLOAT64");
        }
        return (Column<Double>) column;
    }
}
