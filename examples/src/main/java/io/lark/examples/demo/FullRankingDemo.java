// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo;

import io.lark.examples.demo.column.ColumnScoring;
import io.lark.examples.demo.dag.RequestLoader;
import io.lark.examples.demo.model.ColumnStage;
import io.lark.examples.demo.model.DemoResult;
import io.lark.examples.demo.model.Prepared;
import io.lark.examples.demo.model.ScoredGoods;
import io.lark.examples.demo.rank.RankingStages;
import io.lark.rank.RankedSlot;
import io.lark.rank.RankingResult;

/**
 * End-to-end demo: DAG request loading, columnar metrics with per-PV top-k,
 * then greedy and beam-search slot ranking over the selected goods.
 *
 * <p>Each stage lives in its own package:
 *
 * <ul>
 *   <li>{@code demo.dag} — Node layer request/config loading and validation;</li>
 *   <li>{@code demo.column} — ColumnModule metrics, experiment score and
 *       top-k;</li>
 *   <li>{@code demo.rank} — greedy and beam ranking with admissions;</li>
 *   <li>{@code demo.model} — shared records between stages.</li>
 * </ul>
 *
 * <p>Run with {@code ./gradlew :examples:runFullDemo}. For warmed-up
 * throughput numbers use {@code :examples:runFullBenchmark}.
 */
public final class FullRankingDemo {

    private static final int CANDIDATES = 300;

    private FullRankingDemo() {
    }

    public static void main(String[] args) {
        print(run(CANDIDATES));
    }

    /** Run the demo once and return every intermediate stage. */
    public static DemoResult run(int candidateCount) {
        Prepared prepared = RequestLoader.load(candidateCount);
        ColumnStage column = ColumnScoring.score(prepared.request(),
                prepared.config());
        var candidates = column.candidates();
        return new DemoResult(
                prepared,
                column,
                RankingStages.greedy(candidates, prepared.config())
                        .rankWithStats(candidates, prepared.config()),
                RankingStages.beam(candidates, prepared.config())
                        .rankWithStats(candidates, prepared.config()));
    }

    /** Print one complete demo run. */
    public static void print(DemoResult result) {
        printDag(result.prepared());
        printColumn(result.column());
        printRanking("greedy", result.greedy());
        printRanking("beam width=" + result.prepared().config().beamWidth(),
                result.beam());
    }

    private static void printDag(Prepared prepared) {
        System.out.println("=== DAG stage ===");
        System.out.print(prepared.dagStats().summary());
    }

    private static void printColumn(ColumnStage column) {
        System.out.println("=== column stage ===");
        System.out.printf("selected=%d/%d candidates, retarget_ad=%s, "
                        + "delay=%s%n",
                column.selectedCount(), column.candidates().size(),
                column.retargetAd().shape(), column.delay().shape());
        System.out.println(column.stats().summary());
    }

    private static void printRanking(String title,
                                     RankingResult<ScoredGoods> result) {
        System.out.printf("%n=== %s ===%n", title);
        System.out.printf("%5s %8s %10s %6s %9s %10s%n",
                "slot", "goods", "category", "brand", "final", "delta");
        for (RankedSlot<ScoredGoods> slot : result.slots()) {
            ScoredGoods item = slot.item();
            System.out.printf("%5d %8d %10d %6d %9.3f %10.3f%n",
                    slot.slot() + 1, item.goods().goodsId(),
                    item.category(), item.brand(), slot.score(),
                    slot.score() - item.baseScore());
        }
        System.out.printf("stats: filled=%d/%d, admitted=%d, scores=%d, "
                        + "sorts=%d, elapsed=%.3fms, timedOut=%s%n",
                result.stats().slotsFilled(),
                result.stats().slotsRequested(),
                result.stats().admittedTotal(),
                result.stats().scoreCalls(),
                result.stats().sortCalls(),
                result.stats().elapsedNanos() / 1_000_000.0,
                result.stats().timedOut());
    }
}
