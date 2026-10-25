// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk;

import io.lark.column.Row;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.examples.risk.data.TransactionRepository;
import io.lark.examples.risk.module.RiskReportModule;
import io.lark.examples.risk.module.RiskScoreModule;
import io.lark.examples.risk.module.TransactionFeatureModule;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Realistic payment risk engine demo.
 *
 * <p>This package is intentionally split into independent columns, nodes,
 * modules, repository and printing classes, mirroring a normal Java service
 * rather than a single-file sample.
 *
 * <p>Run with {@code ./gradlew :examples:runRisk}.
 */
public final class RiskEngineDemo {

    private RiskEngineDemo() {
    }

    public static void main(String[] args) {
        TransactionFeatureModule features = new TransactionFeatureModule();
        RiskScoreModule risk = new RiskScoreModule(features);
        RiskReportModule report = new RiskReportModule(risk);

        AtomicInteger fallbackEvents = new AtomicInteger();
        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
            pipeline.add(features).add(risk).add(report);
            pipeline.setMonitor(event -> {
                if ("node.fallback".equals(event.action())) {
                    fallbackEvents.incrementAndGet();
                }
            });
            pipeline.compile();

            pipeline.feed(FeedStream.of(new ColumnStream<?>[]{
                    features.userId(),
                    features.amountFen(),
                    features.txnCount(),
                    features.chargebackCount(),
                    features.merchantId()
            }, TransactionRepository.transactions()));

            pipeline.compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    features.userId(),
                    features.amount().out(),
                    risk.score().out(),
                    risk.band().level(),
                    risk.approval().out(),
                    features.merchantTier().out()));

            RiskResultPrinter.printTable(rows);
            System.out.println("total_risk_score = "
                    + pipeline.fetchScalar(report.summary().total()));
            System.out.println("mean_risk_score  = "
                    + pipeline.fetchScalar(report.summary().mean()));
            System.out.println("max_risk_score   = "
                    + pipeline.fetchScalar(report.summary().max()));
            System.out.println("high_risk_count  = "
                    + pipeline.fetchScalar(report.highRisk().out()));
            System.out.println("fallback events  = " + fallbackEvents.get());
            System.out.print(pipeline.stats().summary());
        }
    }
}
