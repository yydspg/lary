// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.recsys.node.FunnelSummaryNode;
import io.lark.examples.recsys.node.PortfolioSummaryNode;
import io.lark.examples.recsys.node.RankingSummaryNode;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Portfolio-level summary over the per-row metrics. */
@ColumnComponent
public final class PerformanceSummaryModule extends ColumnModule {

    private final FunnelMetricModule metrics;
    private final RankingWeightModule rankingModule;
    private FunnelSummaryNode funnel;
    private PortfolioSummaryNode portfolio;
    private RankingSummaryNode ranking;

    public PerformanceSummaryModule(FunnelMetricModule metrics,
                                    RankingWeightModule ranking) {
        this.metrics = metrics;
        this.rankingModule = ranking;
    }

    @Override
    protected void define() {
        funnel = node(new FunnelSummaryNode(
                metrics.published(RecSysMetrics.IMPRESSIONS),
                metrics.published(RecSysMetrics.CLICKS),
                metrics.published(RecSysMetrics.ORDERS)));
        portfolio = node(new PortfolioSummaryNode(
                metrics.published(RecSysMetrics.GMV),
                metrics.published(RecSysMetrics.COUPON_COST),
                metrics.published(RecSysMetrics.BUY_AGAIN_ORDERS)));
        ranking = node(new RankingSummaryNode(),
                rankingModule.published(RecSysMetrics.WEIGHT));
        dependsOn(FunnelMetricModule.class, RankingWeightModule.class);
        output(funnel.overallCtr(), funnel.overallCvr(),
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
