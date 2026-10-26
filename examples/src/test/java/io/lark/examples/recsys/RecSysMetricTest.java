// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys;

import io.lark.column.data.Row;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.examples.recsys.data.MetricsFeed;
import io.lark.examples.recsys.module.FunnelMetricModule;
import io.lark.examples.recsys.module.PerformanceSummaryModule;
import io.lark.examples.recsys.module.RankingWeightModule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecSysMetricTest {

    @Test
    void computesFunnelCommercialAndRankingMetrics() {
        FunnelMetricModule metrics = new FunnelMetricModule();
        RankingWeightModule ranking = new RankingWeightModule(metrics);
        PerformanceSummaryModule summary =
                new PerformanceSummaryModule(metrics, ranking);

        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(metrics).add(ranking).add(summary);
            pipeline.compile();
            pipeline.feed(MetricsFeed.sample(metrics));
            pipeline.compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    metrics.impressions(),
                    metrics.clicks(),
                    metrics.ctr(),
                    metrics.cvr(),
                    metrics.gmv(),
                    metrics.buyAgainCost(),
                    metrics.couponRoi(),
                    ranking.weight()));

            assertEquals(6, rows.size());
            assertEquals(0.05, rows.get(0).get(2).asDouble(), 1e-9);
            assertEquals(0.10, rows.get(0).get(3).asDouble(), 1e-9);
            assertEquals(500.0, rows.get(0).get(4).asDouble(), 1e-9);
            assertEquals(10.0, rows.get(0).get(5).asDouble(), 1e-9);
            assertEquals(50.0, rows.get(0).get(6).asDouble(), 1e-9);
            assertEquals(35.0, rows.get(0).get(7).asDouble(), 1e-9);

            // Zero repeat orders must produce 0, not infinity/NaN.
            assertEquals(0.0, rows.get(2).get(5).asDouble(), 1e-9);
            assertEquals(27.8, rows.get(2).get(7).asDouble(), 1e-9);

            assertEquals(83.333_333, rows.get(5).get(6).asDouble(), 1e-5);
            assertEquals(81.666_666, rows.get(5).get(7).asDouble(), 1e-5);

            assertEquals(410.0 / 7_000.0,
                    pipeline.fetchScalar(summary.overallCtr()), 1e-9);
            assertEquals(0.20, pipeline.fetchScalar(summary.overallCvr()), 1e-9);
            assertEquals(13_280.0, pipeline.fetchScalar(summary.totalGmv()), 1e-9);
            assertEquals(192.0,
                    pipeline.fetchScalar(summary.totalCouponCost()), 1e-9);
            assertEquals(69.166_666, pipeline.fetchScalar(summary.overallRoi()), 1e-5);
            assertEquals(192.0 / 23.0,
                    pipeline.fetchScalar(summary.overallBuyAgainCost()), 1e-6);
            assertEquals(63.202_777,
                    pipeline.fetchScalar(summary.averageWeight()), 1e-5);
        }
    }

    @Test
    void mockFeedCoversZeroDenominators() {
        FunnelMetricModule metrics = new FunnelMetricModule();
        RankingWeightModule ranking = new RankingWeightModule(metrics);

        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(metrics).add(ranking);
            pipeline.compile();
            pipeline.feed(MetricsFeed.of(metrics, MockMetrics.rows()));
            pipeline.compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    metrics.ctr(), metrics.cvr(), metrics.gmv(),
                    metrics.buyAgainCost(), metrics.couponRoi(),
                    ranking.weight()));

            assertEquals(0.10, rows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(0.20, rows.get(0).get(1).asDouble(), 1e-9);
            assertEquals(100.0, rows.get(0).get(2).asDouble(), 1e-9);
            assertEquals(1.0, rows.get(0).get(3).asDouble(), 1e-9);
            assertEquals(100.0, rows.get(0).get(4).asDouble(), 1e-9);
            assertEquals(70.5, rows.get(0).get(5).asDouble(), 1e-9);

            // All-zero row: every ratio is defined as zero.
            assertEquals(0.0, rows.get(1).get(0).asDouble(), 1e-9);
            assertEquals(0.0, rows.get(1).get(1).asDouble(), 1e-9);
            assertEquals(0.0, rows.get(1).get(3).asDouble(), 1e-9);
            assertEquals(0.0, rows.get(1).get(4).asDouble(), 1e-9);

            // Zero coupon cost / zero repeat orders.
            assertEquals(0.0, rows.get(2).get(3).asDouble(), 1e-9);
            assertEquals(0.0, rows.get(2).get(4).asDouble(), 1e-9);
            assertEquals(20.5, rows.get(2).get(5).asDouble(), 1e-9);
        }
    }
}
