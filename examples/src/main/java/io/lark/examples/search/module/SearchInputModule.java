// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.schema.SearchMetrics;

/** Declares all external search input handles used to build a feed stream. */
@ColumnComponent
public final class SearchInputModule extends ColumnModule {

    private ColumnStream<Long> pvId;
    private ColumnStream<Long> goodsId;
    private ColumnStream<Double> perturbation;
    private ColumnStream<Double> originalScore;
    private ColumnStream<Long> impressions;
    private ColumnStream<Long> clicks;
    private ColumnStream<Long> orders;
    private ColumnStream<Long> gmvFen;
    private ColumnStream<Long> couponCostFen;
    private ColumnStream<Long> buyAgainOrders;
    private ColumnStream<Long> delayedOrders;
    private ColumnStream<Long> delayedGmvFen;
    private ColumnStream<Long> retargetClicks;
    private ColumnStream<Long> retargetCostFen;

    @Override
    protected void define() {
        pvId = feed(SearchMetrics.PV_ID);
        goodsId = feed(SearchMetrics.GOODS_ID);
        perturbation = feed(SearchMetrics.PERTURBATION);
        originalScore = feed(SearchMetrics.ORIGINAL_SCORE);
        impressions = feed(SearchMetrics.IMPRESSIONS);
        clicks = feed(SearchMetrics.CLICKS);
        orders = feed(SearchMetrics.ORDERS);
        gmvFen = feed(SearchMetrics.GMV_FEN);
        couponCostFen = feed(SearchMetrics.COUPON_COST_FEN);
        buyAgainOrders = feed(SearchMetrics.BUY_AGAIN_ORDERS);
        delayedOrders = feed(SearchMetrics.DELAYED_ORDERS);
        delayedGmvFen = feed(SearchMetrics.DELAYED_GMV_FEN);
        retargetClicks = feed(SearchMetrics.RETARGET_CLICKS);
        retargetCostFen = feed(SearchMetrics.RETARGET_COST_FEN);
        // Row identity is part of the result boundary: callers fetch it back
        // after PV-level selection to rebuild the ranked goods list.
        output(pvId, goodsId);
    }

    public ColumnStream<Long> pvId() {
        return pvId;
    }

    public ColumnStream<Long> goodsId() {
        return goodsId;
    }

    public ColumnStream<Double> perturbation() {
        return perturbation;
    }

    public ColumnStream<Double> originalScore() {
        return originalScore;
    }

    public ColumnStream<Long> impressions() {
        return impressions;
    }

    public ColumnStream<Long> clicks() {
        return clicks;
    }

    public ColumnStream<Long> orders() {
        return orders;
    }

    public ColumnStream<Long> gmvFen() {
        return gmvFen;
    }

    public ColumnStream<Long> couponCostFen() {
        return couponCostFen;
    }

    public ColumnStream<Long> buyAgainOrders() {
        return buyAgainOrders;
    }

    public ColumnStream<Long> delayedOrders() {
        return delayedOrders;
    }

    public ColumnStream<Long> delayedGmvFen() {
        return delayedGmvFen;
    }

    public ColumnStream<Long> retargetClicks() {
        return retargetClicks;
    }

    public ColumnStream<Long> retargetCostFen() {
        return retargetCostFen;
    }
}
