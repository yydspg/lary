// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.example;

import io.lark.column.data.Row;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.examples.recsys.MetricResultPrinter;
import io.lark.examples.recsys.data.MetricsFeed;
import io.lark.examples.recsys.module.FunnelMetricModule;
import io.lark.examples.recsys.module.PerformanceSummaryModule;
import io.lark.examples.recsys.module.RankingWeightModule;
import io.lark.spring.ColumnPipelineFactory;
import io.lark.spring.EnableLaryColumns;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The recsys demo running as a Spring Boot application.
 *
 * <p>{@link EnableLaryColumns} discovers the {@code @ColumnComponent} modules;
 * Spring resolves their constructor dependencies and the auto-configuration
 * exposes a {@link ColumnPipelineFactory}.
 *
 * <p>Run with {@code ./gradlew :spring:runSpringRecsys}.
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableLaryColumns(basePackages = "io.lark.examples.recsys.module")
public class SpringRecSysDemo {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context =
                     SpringApplication.run(SpringRecSysDemo.class, args)) {
            ColumnPipelineFactory pipelines =
                    context.getBean(ColumnPipelineFactory.class);
            FunnelMetricModule metrics = context.getBean(FunnelMetricModule.class);
            RankingWeightModule ranking =
                    context.getBean(RankingWeightModule.class);
            PerformanceSummaryModule summary =
                    context.getBean(PerformanceSummaryModule.class);

            try (ColumnPipeline pipeline = pipelines.create()) {
                pipeline.compile()
                        .feed(MetricsFeed.sample(metrics))
                        .compute();

                List<Row> rows = pipeline.fetch(FetchStream.of(
                        metrics.impressions(),
                        metrics.clicks(),
                        metrics.ctr(),
                        metrics.cvr(),
                        metrics.gmv(),
                        metrics.buyAgainCost(),
                        metrics.couponRoi(),
                        ranking.weight()));

                System.out.println("=== spring / recommendation metrics ===");
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
}
