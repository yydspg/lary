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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskEngineTest {

    @Test
    void scoresTransactionsAndAggregatesThePortfolio() {
        TransactionFeatureModule features = new TransactionFeatureModule();
        RiskScoreModule risk = new RiskScoreModule(features);
        RiskReportModule report = new RiskReportModule(risk);

        AtomicInteger fallbackEvents = new AtomicInteger();
        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(features).add(risk).add(report);
            pipeline.setMonitor(event -> {
                if ("node.fallback".equals(event.action())) {
                    fallbackEvents.incrementAndGet();
                }
            });
            pipeline.compile();

            pipeline.feed(FeedStream.of(new ColumnStream<?>[]{
                    features.userId(), features.amountFen(), features.txnCount(),
                    features.chargebackCount(), features.merchantId()
            }, TransactionRepository.transactions()));
            pipeline.compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    features.userId(),
                    features.amount().out(),
                    risk.score().out(),
                    risk.band().level(),
                    risk.approval().out(),
                    features.merchantTier().out()));

            assertEquals(6, rows.size());
            assertEquals(1_200.0, rows.get(0).get(1).asDouble(), 1e-9);
            assertEquals(486.666_666, rows.get(0).get(2).asDouble(), 1e-5);
            assertEquals(1, rows.get(0).get(3).asLong());
            assertEquals(1, rows.get(0).get(4).asLong());
            assertEquals(1, rows.get(0).get(5).asLong());

            assertEquals(10_024.166_666, rows.get(3).get(2).asDouble(), 1e-5);
            assertEquals(2, rows.get(3).get(3).asLong());
            assertEquals(0, rows.get(3).get(4).asLong());

            assertEquals(20_169.166_666,
                    pipeline.fetchScalar(report.summary().total()), 1e-5);
            assertEquals(3_361.527_777,
                    pipeline.fetchScalar(report.summary().mean()), 1e-5);
            assertEquals(10_024.166_666,
                    pipeline.fetchScalar(report.summary().max()), 1e-5);
            assertEquals(3.0,
                    pipeline.fetchScalar(report.highRisk().out()), 1e-9);
            assertEquals(1, fallbackEvents.get());
        }
    }
}
