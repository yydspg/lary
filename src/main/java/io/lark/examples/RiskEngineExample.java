// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.DType;
import io.lark.column.Row;
import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.column.biz.ScaleNode;
import io.lark.column.compute.Compute;
import io.lark.metric.Event;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Realistic risk-engine example:
 *
 * <pre>
 * raw rows
 *   └─ TransactionFeatureModule
 *        ├─ amount_fen → amount_yuan
 *        ├─ txn_count_24h → txn_velocity
 *        ├─ chargeback_count / txn_count → chargeback_ratio
 *        ├─ weighted_amount
 *        └─ merchant_tier (external enrichment, fallback)
 *   └─ RiskScoreModule
 *        ├─ risk_score
 *        ├─ risk_level (LOW / MEDIUM / HIGH)
 *        ├─ high_mask
 *        └─ approved
 *   └─ RiskReportModule
 *        ├─ total/mean/max risk score
 *        └─ high-risk count
 * </pre>
 *
 * <p>Run with {@code ./gradlew runRisk}.
 */
public final class RiskEngineExample {

    // ─────────────────────────────────────────────────────────────────────
    // Transaction feature module
    // ─────────────────────────────────────────────────────────────────────

    static final class AmountToYuanNode extends ColumnNode {
        final ColumnStream out = output("amount_yuan");

        AmountToYuanNode(ColumnStream amountFen) {
            super(amountFen);
        }

        @Override
        public void compute(ColumnContext ctx) {
            // fen → yuan is a non-integer conversion, so keep float64.
            ctx.write(out, Compute.divScalar(ctx.read(feeds()[0]), 100.0));
        }
    }

    static final class TxnVelocityNode extends ColumnNode {
        final ColumnStream out = output("txn_velocity");

        TxnVelocityNode(ColumnStream txnCount24h) {
            super(txnCount24h);
        }

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.divScalar(ctx.read(feeds()[0]), 24.0));
        }
    }

    static final class ChargebackRatioNode extends ColumnNode {
        final ColumnStream out = output("chargeback_ratio");

        ChargebackRatioNode(ColumnStream chargebackCount, ColumnStream txnCount) {
            super(chargebackCount, txnCount);
        }

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.div(ctx.read(feeds()[0]), ctx.read(feeds()[1])));
        }
    }

    /**
     * Simulates an external merchant-enrichment call that may fail. The
     * fallback publishes a conservative default tier instead of blocking the
     * whole risk pipeline.
     */
    static final class MerchantTierNode extends ColumnNode {
        final ColumnStream out = output("merchant_tier");

        MerchantTierNode(ColumnStream merchantId) {
            super(merchantId);
        }

        @Override
        public void compute(ColumnContext ctx) {
            int merchantId = (int) ctx.read(feeds()[0]).getLong(0);
            throw new IllegalStateException(
                    "merchant service unavailable for merchant " + merchantId);
        }

        @Override
        public boolean fallback(ColumnContext ctx, Throwable error) {
            int rows = ctx.read(feeds()[0]).size();
            ctx.write(out, Tensor.full(rows, 1.0, DType.INT64));
            return true;
        }
    }

    static final class TransactionFeatureModule extends ColumnModule {
        final ColumnStream userId = input("user_id");
        final ColumnStream amountFen = input("amount_fen");
        final ColumnStream txnCount = input("txn_count_24h");
        final ColumnStream chargebackCount = input("chargeback_count");
        final ColumnStream merchantId = input("merchant_id");

        final AmountToYuanNode amount = add(new AmountToYuanNode(amountFen));
        final TxnVelocityNode velocity = add(new TxnVelocityNode(txnCount));
        final ChargebackRatioNode ratio = add(
                new ChargebackRatioNode(chargebackCount, txnCount));
        final ScaleNode weighted = add(new ScaleNode(amount.out, "weighted_amount", 0.4));
        final MerchantTierNode tier = add(new MerchantTierNode(merchantId));

        {
            output(userId, amount.out, velocity.out, ratio.out,
                    weighted.out, tier.out);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Risk scoring module
    // ─────────────────────────────────────────────────────────────────────

    static final class RiskScoreNode extends ColumnNode {
        final ColumnStream out = output("risk_score");

        RiskScoreNode(ColumnStream weightedAmount, ColumnStream velocity,
                      ColumnStream chargebackRatio, ColumnStream merchantTier) {
            super(weightedAmount, velocity, chargebackRatio, merchantTier);
        }

        @Override
        public void compute(ColumnContext ctx) {
            Tensor weighted = ctx.read(feeds()[0]);
            Tensor velocity = Compute.mulScalar(ctx.read(feeds()[1]), 10.0);
            Tensor chargeback = Compute.mulScalar(ctx.read(feeds()[2]), 100.0);
            Tensor tier = Compute.mulScalar(ctx.read(feeds()[3]), 5.0);
            Tensor score = Compute.add(
                    Compute.add(weighted, velocity),
                    Compute.add(chargeback, tier));
            ctx.write(out, score);
        }
    }

    static final class RiskBandNode extends ColumnNode {
        final ColumnStream level = output("risk_level");
        final ColumnStream highMask = output("high_mask");

        RiskBandNode(ColumnStream riskScore) {
            super(riskScore);
        }

        @Override
        public void compute(ColumnContext ctx) {
            Tensor score = ctx.read(feeds()[0]);
            int rows = score.size();
            Tensor high = Compute.gtScalar(score, 1000.0);
            Tensor medium = Compute.gtScalar(score, 200.0);
            Tensor low = Tensor.full(rows, 0, DType.INT64);
            Tensor mid = Tensor.full(rows, 1, DType.INT64);
            Tensor highValue = Tensor.full(rows, 2, DType.INT64);

            // HIGH > 1000, MEDIUM > 200, otherwise LOW.
            Tensor levelValue = Compute.select(high, highValue,
                    Compute.select(medium, mid, low));
            ctx.write(highMask, high);
            ctx.write(level, levelValue);
        }
    }

    static final class ApprovalNode extends ColumnNode {
        final ColumnStream out = output("approved");

        ApprovalNode(ColumnStream riskScore) {
            super(riskScore);
        }

        @Override
        public void compute(ColumnContext ctx) {
            // approved = risk_score < 1000
            ctx.write(out, Compute.ltScalar(ctx.read(feeds()[0]), 1000.0));
        }
    }

    static final class RiskScoreModule extends ColumnModule {
        final RiskScoreNode score;
        final RiskBandNode band;
        final ApprovalNode approval;

        RiskScoreModule(TransactionFeatureModule features) {
            score = add(new RiskScoreNode(
                    features.weighted.out, features.velocity.out,
                    features.ratio.out, features.tier.out));
            band = add(new RiskBandNode(score.out));
            approval = add(new ApprovalNode(score.out));
            dependsOn(TransactionFeatureModule.class);
            output(score.out, band.level, band.highMask, approval.out);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Report module
    // ─────────────────────────────────────────────────────────────────────

    static final class ScoreSummaryNode extends ColumnNode {
        final ColumnStream total = output("total_risk_score");
        final ColumnStream mean = output("mean_risk_score");
        final ColumnStream max = output("max_risk_score");

        ScoreSummaryNode(ColumnStream riskScore) {
            super(riskScore);
        }

        @Override
        public void compute(ColumnContext ctx) {
            Tensor score = ctx.read(feeds()[0]);
            ctx.write(total, Compute.sumTensor(score));
            ctx.write(mean, Compute.meanTensor(score));
            ctx.write(max, Compute.maxTensor(score));
        }
    }

    static final class HighRiskCountNode extends ColumnNode {
        final ColumnStream out = output("high_risk_count");

        HighRiskCountNode(ColumnStream highMask) {
            super(highMask);
        }

        @Override
        public void compute(ColumnContext ctx) {
            ctx.write(out, Compute.sumTensor(ctx.read(feeds()[0])));
        }
    }

    static final class RiskReportModule extends ColumnModule {
        final ScoreSummaryNode summary;
        final HighRiskCountNode highRisk;

        RiskReportModule(RiskScoreModule risk) {
            summary = add(new ScoreSummaryNode(risk.score.out));
            highRisk = add(new HighRiskCountNode(risk.band.highMask));
            dependsOn(RiskScoreModule.class);
            output(summary.total, summary.mean, summary.max, highRisk.out);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Entry point
    // ─────────────────────────────────────────────────────────────────────

    private RiskEngineExample() {
    }

    public static void main(String[] args) {
        TransactionFeatureModule features = new TransactionFeatureModule();
        RiskScoreModule risk = new RiskScoreModule(features);
        RiskReportModule report = new RiskReportModule(risk);

        AtomicInteger fallbackEvents = new AtomicInteger();
        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
            pipeline.add(features).add(risk).add(report);
            pipeline.setMonitor((Event event) -> {
                if ("node.fallback".equals(event.action())) {
                    fallbackEvents.incrementAndGet();
                }
            });
            pipeline.compile();

            pipeline.feed(FeedStream.of(
                    new ColumnStream[]{
                            features.userId, features.amountFen,
                            features.txnCount, features.chargebackCount,
                            features.merchantId
                    },
                    transactions()));

            pipeline.compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    features.userId, features.amount.out,
                    risk.score.out, risk.band.level, risk.approval.out,
                    features.tier.out));
            printTable(rows);

            System.out.println("total_risk_score = "
                    + pipeline.fetchScalar("total_risk_score"));
            System.out.println("mean_risk_score  = "
                    + pipeline.fetchScalar("mean_risk_score"));
            System.out.println("max_risk_score   = "
                    + pipeline.fetchScalar("max_risk_score"));
            System.out.println("high_risk_count  = "
                    + pipeline.fetchScalar("high_risk_count"));
            System.out.println("fallback events  = " + fallbackEvents.get());
            System.out.print(pipeline.stats().summary());
        }
    }

    private static List<Row> transactions() {
        // user_id, amount_fen, txn_count_24h, chargeback_count, merchant_id
        return List.of(
                Row.of(Cell.of(1001), Cell.of(120_000), Cell.of(4), Cell.of(0), Cell.of(7)),
                Row.of(Cell.of(1002), Cell.of(850_000), Cell.of(12), Cell.of(1), Cell.of(9)),
                Row.of(Cell.of(1003), Cell.of(5_000), Cell.of(1), Cell.of(0), Cell.of(7)),
                Row.of(Cell.of(1004), Cell.of(2_500_000), Cell.of(30), Cell.of(2), Cell.of(3)),
                Row.of(Cell.of(1005), Cell.of(45_000), Cell.of(3), Cell.of(0), Cell.of(7)),
                Row.of(Cell.of(1006), Cell.of(1_500_000), Cell.of(20), Cell.of(3), Cell.of(9)));
    }

    private static void printTable(List<Row> rows) {
        System.out.println("\n=== payment risk engine ===");
        System.out.printf("%-8s %12s %12s %8s %10s %6s%n",
                "user", "amount_yuan", "risk_score", "level", "approved", "tier");
        for (Row row : rows) {
            System.out.printf("%-8d %12.2f %12.2f %8d %10d %6d%n",
                    row.get(0).asLong(),
                    row.get(1).asDouble(),
                    row.get(2).asDouble(),
                    row.get(3).asLong(),
                    row.get(4).asLong(),
                    row.get(5).asLong());
        }
    }
}
