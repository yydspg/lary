// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.rank;

import io.lark.examples.demo.model.RankConfig;
import io.lark.examples.demo.model.ScoredGoods;
import io.lark.rank.BeamSearchRanker;
import io.lark.rank.IndexedSlotScorer;
import io.lark.rank.Ranker;
import io.lark.rank.SequentialRanker;
import io.lark.rank.SlotAdmission;

import java.time.Duration;
import java.util.List;

/** Ranking stage: greedy and beam-search slates over column-selected goods. */
public final class RankingStages {

    private RankingStages() {
    }

    public static Ranker<ScoredGoods, RankConfig> greedy(
            List<ScoredGoods> candidates, RankConfig config) {
        return SequentialRanker
                .<ScoredGoods, RankConfig>builder(config.topK())
                .admissions(admissions())
                .scorer(indexedScorer(candidates))
                .maxDuration(Duration.ofMillis(100))
                .build();
    }

    public static Ranker<ScoredGoods, RankConfig> beam(
            List<ScoredGoods> candidates, RankConfig config) {
        return BeamSearchRanker
                .<ScoredGoods, RankConfig>builder(config.topK(),
                        config.beamWidth())
                .branch(config.beamWidth())
                .admissions(admissions())
                .scorer(indexedScorer(candidates))
                .maxDuration(Duration.ofMillis(150))
                .build();
    }

    private static List<SlotAdmission<ScoredGoods, RankConfig>> admissions() {
        return List.of(
                (ctx, item) -> ctx.slot() >= 3 || !item.restricted(),
                (ctx, item) -> ctx.gapSince(other ->
                        other.category() == item.category()) >= 2,
                (ctx, item) -> ctx.selectedCount(other ->
                        other.brand() == item.brand()) < 4,
                (ctx, item) -> item.cost() <= ctx.businessContext().budget());
    }

    /**
     * Index-based scorer: the ranker keeps an {@code int[]} of admitted
     * candidates and never allocates an intermediate candidate list.
     */
    private static IndexedSlotScorer<ScoredGoods, RankConfig> indexedScorer(
            List<ScoredGoods> candidates) {
        return (ctx, indices, count) -> {
            double[] scores = new double[count];
            for (int i = 0; i < count; i++) {
                ScoredGoods item = candidates.get(indices[i]);
                int sameCategory = ctx.selectedCount(other ->
                        other.category() == item.category());
                int sameBrand = ctx.selectedCount(other ->
                        other.brand() == item.brand());
                scores[i] = item.baseScore()
                        - sameCategory * 1.5
                        - sameBrand * 0.8;
            }
            return scores;
        };
    }
}
