// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnModule;
import io.lark.examples.recsys.columns.AverageWeight;
import io.lark.examples.recsys.columns.OverallBuyAgainCost;
import io.lark.examples.recsys.columns.OverallCtr;
import io.lark.examples.recsys.columns.OverallCvr;
import io.lark.examples.recsys.columns.OverallRoi;
import io.lark.examples.recsys.columns.TotalCouponCost;
import io.lark.examples.recsys.columns.TotalGmv;
import io.lark.examples.recsys.node.FunnelSummaryNode;
import io.lark.examples.recsys.node.PortfolioSummaryNode;
import io.lark.examples.recsys.node.RankingSummaryNode;

/** Portfolio-level summary over the per-row metrics. */
public final class PerformanceSummaryModule extends ColumnModule {

    private final FunnelSummaryNode funnel;
    private final PortfolioSummaryNode portfolio;
    private final RankingSummaryNode ranking;

    public PerformanceSummaryModule(FunnelMetricModule metrics,
                                    RankingWeightModule weight) {
        funnel = add(new FunnelSummaryNode(
                metrics.impressions(), metrics.clicks(), metrics.orders()));
        portfolio = add(new PortfolioSummaryNode(
                metrics.gmv(), metrics.couponCost(), metrics.buyAgainOrders()));
        ranking = add(new RankingSummaryNode(weight.weight()));
        dependsOn(FunnelMetricModule.class, RankingWeightModule.class);
        output(funnel.overallCtr(), funnel.overallCvr(),
                portfolio.totalGmv(), portfolio.totalCouponCost(),
                portfolio.overallRoi(), portfolio.overallBuyAgainCost(),
                ranking.out());
    }

    public OverallCtr overallCtr() {
        return funnel.overallCtr();
    }

    public OverallCvr overallCvr() {
        return funnel.overallCvr();
    }

    public TotalGmv totalGmv() {
        return portfolio.totalGmv();
    }

    public TotalCouponCost totalCouponCost() {
        return portfolio.totalCouponCost();
    }

    public OverallRoi overallRoi() {
        return portfolio.overallRoi();
    }

    public OverallBuyAgainCost overallBuyAgainCost() {
        return portfolio.overallBuyAgainCost();
    }

    public AverageWeight averageWeight() {
        return ranking.out();
    }
}
