// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnModule;
import io.lark.examples.recsys.columns.BuyAgainCost;
import io.lark.examples.recsys.columns.BuyAgainOrders;
import io.lark.examples.recsys.columns.Clicks;
import io.lark.examples.recsys.columns.CouponCost;
import io.lark.examples.recsys.columns.CouponCostFen;
import io.lark.examples.recsys.columns.CouponRoi;
import io.lark.examples.recsys.columns.Ctr;
import io.lark.examples.recsys.columns.Cvr;
import io.lark.examples.recsys.columns.Gmv;
import io.lark.examples.recsys.columns.GmvFen;
import io.lark.examples.recsys.columns.Impressions;
import io.lark.examples.recsys.columns.Orders;
import io.lark.examples.recsys.node.BuyAgainCostNode;
import io.lark.examples.recsys.node.CouponCostNode;
import io.lark.examples.recsys.node.CouponRoiNode;
import io.lark.examples.recsys.node.CtrNode;
import io.lark.examples.recsys.node.CvrNode;
import io.lark.examples.recsys.node.GmvNode;

/** Computes the core search/recommendation funnel and commercial metrics. */
public final class FunnelMetricModule extends ColumnModule {

    private final Impressions impressions = input(Impressions.class);
    private final Clicks clicks = input(Clicks.class);
    private final Orders orders = input(Orders.class);
    private final GmvFen gmvFen = input(GmvFen.class);
    private final CouponCostFen couponCostFen = input(CouponCostFen.class);
    private final BuyAgainOrders buyAgainOrders = input(BuyAgainOrders.class);

    private final CtrNode ctr = add(new CtrNode(clicks, impressions));
    private final CvrNode cvr = add(new CvrNode(orders, clicks));
    private final GmvNode gmv = add(new GmvNode(gmvFen));
    private final CouponCostNode couponCost = add(new CouponCostNode(couponCostFen));
    private final BuyAgainCostNode buyAgainCost =
            add(new BuyAgainCostNode(couponCost.out(), buyAgainOrders));
    private final CouponRoiNode couponRoi =
            add(new CouponRoiNode(gmv.out(), couponCost.out()));

    public FunnelMetricModule() {
        output(impressions, clicks, orders, ctr.out(), cvr.out(), gmv.out(),
                buyAgainCost.out(), couponRoi.out());
    }

    public Impressions impressions() {
        return impressions;
    }

    public Clicks clicks() {
        return clicks;
    }

    public Orders orders() {
        return orders;
    }

    public GmvFen gmvFen() {
        return gmvFen;
    }

    public CouponCostFen couponCostFen() {
        return couponCostFen;
    }

    public BuyAgainOrders buyAgainOrders() {
        return buyAgainOrders;
    }

    public Ctr ctr() {
        return ctr.out();
    }

    public Cvr cvr() {
        return cvr.out();
    }

    public Gmv gmv() {
        return gmv.out();
    }

    public CouponCost couponCost() {
        return couponCost.out();
    }

    public BuyAgainCost buyAgainCost() {
        return buyAgainCost.out();
    }

    public CouponRoi couponRoi() {
        return couponRoi.out();
    }
}
