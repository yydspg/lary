// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys;

import io.lark.column.Row;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.examples.recsys.data.MetricsFeed;
import io.lark.examples.recsys.module.FunnelMetricModule;
import io.lark.examples.recsys.module.PerformanceSummaryModule;
import io.lark.examples.recsys.module.RankingWeightModule;

import java.util.List;

/**
 * Search/recommendation metric computation demo.
 *
 * <p>The graph computes CTR, CVR, GMV, buy-again cost, coupon ROI and a
 * ranking weight from item-level funnel metrics. It is split into independent
 * columns, nodes, modules and data classes like a normal Java service.
 *
 * <p>Run with {@code ./gradlew :examples:runRecsys}.
 */
public final class RecSysDemo {

    private RecSysDemo() {
    }

    public static void main(String[] args) {
        FunnelMetricModule metrics = new FunnelMetricModule();
        RankingWeightModule ranking = new RankingWeightModule(metrics);
        PerformanceSummaryModule summary =
                new PerformanceSummaryModule(metrics, ranking);

        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
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

            System.out.println("=== search / recommendation metrics ===");
            MetricResultPrinter.printTable(rows);
            System.out.printf("overall_ctr            = %.6f%n",
                    pipeline.fetchScalar(summary.overallCtr()));
            System.out.printf("overall_cvr            = %.6f%n",
                    pipeline.fetchScalar(summary.overallCvr()));
            System.out.printf("total_gmv              = %.2f%n",
                    pipeline.fetchScalar(summary.totalGmv()));
            System.out.printf("total_coupon_cost      = %.2f%n",
                    pipeline.fetchScalar(summary.totalCouponCost()));
            System.out.printf("overall_roi            = %.4f%n",
                    pipeline.fetchScalar(summary.overallRoi()));
            System.out.printf("overall_buyagain_cost  = %.4f%n",
                    pipeline.fetchScalar(summary.overallBuyAgainCost()));
            System.out.printf("average_weight         = %.4f%n",
                    pipeline.fetchScalar(summary.averageWeight()));
            System.out.print(pipeline.stats().summary());
        }
    }
}
