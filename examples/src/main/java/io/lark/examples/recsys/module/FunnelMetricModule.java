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

    private ColumnStream<Long> impressions;
    private ColumnStream<Long> clicks;
    private ColumnStream<Long> orders;
    private ColumnStream<Long> gmvFen;
    private ColumnStream<Long> couponCostFen;
    private ColumnStream<Long> buyAgainOrders;
    private CtrNode ctr;
    private CvrNode cvr;
    private GmvNode gmv;
    private CouponCostNode couponCost;
    private BuyAgainCostNode buyAgainCost;
    private CouponRoiNode couponRoi;

    @Override
    protected void define() {
        impressions = feed(RecSysMetrics.IMPRESSIONS);
        clicks = feed(RecSysMetrics.CLICKS);
        orders = feed(RecSysMetrics.ORDERS);
        gmvFen = feed(RecSysMetrics.GMV_FEN);
        couponCostFen = feed(RecSysMetrics.COUPON_COST_FEN);
        buyAgainOrders = feed(RecSysMetrics.BUY_AGAIN_ORDERS);

        ctr = node(new CtrNode(), clicks, impressions);
        cvr = node(new CvrNode(), orders, clicks);
        gmv = node(new GmvNode(), gmvFen);
        couponCost = node(new CouponCostNode(), couponCostFen);
        buyAgainCost = node(new BuyAgainCostNode(), couponCost.out(), buyAgainOrders);
        couponRoi = node(new CouponRoiNode(), gmv.out(), couponCost.out());
        output(impressions, clicks, orders, buyAgainOrders,
                couponCost.out(), ctr.out(), cvr.out(), gmv.out(),
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
