// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.recsys.node.BuyAgainCostNode;
import io.lark.examples.recsys.node.CouponCostNode;
import io.lark.examples.recsys.node.CouponRoiNode;
import io.lark.examples.recsys.node.CtrNode;
import io.lark.examples.recsys.node.CvrNode;
import io.lark.examples.recsys.node.GmvNode;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Computes the core search/recommendation funnel and commercial metrics. */
@ColumnComponent
public final class FunnelMetricModule extends ColumnModule {

    private final ColumnStream<Long> impressions =
            feed(RecSysMetrics.IMPRESSIONS);
    private final ColumnStream<Long> clicks = feed(RecSysMetrics.CLICKS);
    private final ColumnStream<Long> orders = feed(RecSysMetrics.ORDERS);
    private final ColumnStream<Long> gmvFen = feed(RecSysMetrics.GMV_FEN);
    private final ColumnStream<Long> couponCostFen =
            feed(RecSysMetrics.COUPON_COST_FEN);
    private final ColumnStream<Long> buyAgainOrders =
            feed(RecSysMetrics.BUY_AGAIN_ORDERS);

    private final CtrNode ctr = node(new CtrNode(), clicks, impressions);
    private final CvrNode cvr = node(new CvrNode(), orders, clicks);
    private final GmvNode gmv = node(new GmvNode(), gmvFen);
    private final CouponCostNode couponCost =
            node(new CouponCostNode(), couponCostFen);
    private final BuyAgainCostNode buyAgainCost =
            node(new BuyAgainCostNode(), couponCost.out(), buyAgainOrders);
    private final CouponRoiNode couponRoi =
            node(new CouponRoiNode(), gmv.out(), couponCost.out());

    public FunnelMetricModule() {
        fetch(impressions, clicks, orders, ctr.out(), cvr.out(), gmv.out(),
                buyAgainCost.out(), couponRoi.out());
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

    public ColumnStream<Double> ctr() {
        return ctr.out();
    }

    public ColumnStream<Double> cvr() {
        return cvr.out();
    }

    public ColumnStream<Double> gmv() {
        return gmv.out();
    }

    public ColumnStream<Double> couponCost() {
        return couponCost.out();
    }

    public ColumnStream<Double> buyAgainCost() {
        return buyAgainCost.out();
    }

    public ColumnStream<Double> couponRoi() {
        return couponRoi.out();
    }
}
