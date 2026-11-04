// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.model;

/** One goods candidate with raw metric counters. */
public record GoodsDO(long goodsId, long impressions, long clicks,
                      long orders, long gmvFen, long couponCostFen,
                      long buyAgainOrders, long delayedOrders,
                      long delayedGmvFen, long retargetClicks,
                      long retargetCostFen, double perturbation,
                      double originalScore) {

    public double ctr() {
        return impressions == 0 ? 0.0 : (double) clicks / impressions;
    }

    public double cvr() {
        return clicks == 0 ? 0.0 : (double) orders / clicks;
    }

    public double gmv() {
        return gmvFen / 100.0;
    }

    public double couponCost() {
        return couponCostFen / 100.0;
    }
}
