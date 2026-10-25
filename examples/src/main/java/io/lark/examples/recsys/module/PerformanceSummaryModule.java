// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.recsys.node.FunnelSummaryNode;
import io.lark.examples.recsys.node.PortfolioSummaryNode;
import io.lark.examples.recsys.node.RankingSummaryNode;

/** Portfolio-level summary over the per-row metrics. */
@ColumnComponent
public final class PerformanceSummaryModule extends ColumnModule {

    private final FunnelSummaryNode funnel;
    private final PortfolioSummaryNode portfolio;
    private final RankingSummaryNode ranking;

    public PerformanceSummaryModule(FunnelMetricModule metrics,
                                    RankingWeightModule weight) {
        funnel = node(new FunnelSummaryNode(
                metrics.impressions(), metrics.clicks(), metrics.orders()));
        portfolio = node(new PortfolioSummaryNode(
                metrics.gmv(), metrics.couponCost(), metrics.buyAgainOrders()));
        ranking = node(new RankingSummaryNode(), weight.weight());
        dependsOn(FunnelMetricModule.class, RankingWeightModule.class);
        fetch(funnel.overallCtr(), funnel.overallCvr(),
                portfolio.totalGmv(), portfolio.totalCouponCost(),
                portfolio.overallRoi(), portfolio.overallBuyAgainCost(),
                ranking.out());
    }

    public ColumnStream<Double> overallCtr() {
        return funnel.overallCtr();
    }

    public ColumnStream<Double> overallCvr() {
        return funnel.overallCvr();
    }

    public ColumnStream<Double> totalGmv() {
        return portfolio.totalGmv();
    }

    public ColumnStream<Double> totalCouponCost() {
        return portfolio.totalCouponCost();
    }

    public ColumnStream<Double> overallRoi() {
        return portfolio.overallRoi();
    }

    public ColumnStream<Double> overallBuyAgainCost() {
        return portfolio.overallBuyAgainCost();
    }

    public ColumnStream<Double> averageWeight() {
        return ranking.out();
    }
}
