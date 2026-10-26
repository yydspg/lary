// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.schema.SearchMetrics;

/** Declares all external search input handles used to build a feed stream. */
public final class SearchInputModule extends ColumnModule {

    private final ColumnStream<Long> impressions = feed(SearchMetrics.IMPRESSIONS);
    private final ColumnStream<Long> clicks = feed(SearchMetrics.CLICKS);
    private final ColumnStream<Long> orders = feed(SearchMetrics.ORDERS);
    private final ColumnStream<Long> gmvFen = feed(SearchMetrics.GMV_FEN);
    private final ColumnStream<Long> couponCostFen = feed(SearchMetrics.COUPON_COST_FEN);
    private final ColumnStream<Long> buyAgainOrders = feed(SearchMetrics.BUY_AGAIN_ORDERS);
    private final ColumnStream<Long> delayedOrders = feed(SearchMetrics.DELAYED_ORDERS);
    private final ColumnStream<Long> delayedGmvFen = feed(SearchMetrics.DELAYED_GMV_FEN);
    private final ColumnStream<Long> retargetClicks = feed(SearchMetrics.RETARGET_CLICKS);
    private final ColumnStream<Long> retargetCostFen = feed(SearchMetrics.RETARGET_COST_FEN);

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
