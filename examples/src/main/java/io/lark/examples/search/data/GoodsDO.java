// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

/** One goods row in a search result list. */
public record GoodsDO(
        long goodsId,
        long impressions,
        long clicks,
        long orders,
        long gmvFen,
        long couponCostFen,
        long buyAgainOrders,
        long delayedOrders,
        long delayedGmvFen,
        long retargetClicks,
        long retargetCostFen,
        double perturbation,
        double originalScore) {

    public GoodsDO {
        if (goodsId < 0) {
            throw new IllegalArgumentException("goodsId must be non-negative");
        }
        if (impressions < 0 || clicks < 0 || orders < 0
                || gmvFen < 0 || couponCostFen < 0
                || buyAgainOrders < 0 || delayedOrders < 0
                || delayedGmvFen < 0 || retargetClicks < 0
                || retargetCostFen < 0) {
            throw new IllegalArgumentException("goods counters must be non-negative");
        }
    }

    /** Row-level historical click-through rate. */
    public double ctr() {
        return impressions == 0 ? 0.0 : (double) clicks / impressions;
    }

    /** Row-level historical conversion rate. */
    public double cvr() {
        return clicks == 0 ? 0.0 : (double) orders / clicks;
    }

    /** Row-level GMV in yuan. */
    public double gmv() {
        return gmvFen / 100.0;
    }

    /** Row-level coupon spend in yuan. */
    public double couponCost() {
        return couponCostFen / 100.0;
    }
}
