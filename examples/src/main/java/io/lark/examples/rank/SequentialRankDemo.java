// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.rank;

import io.lark.column.biz.Column;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.F;
import io.lark.column.biz.FeedMapping;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.rank.BeamSearchRanker;
import io.lark.rank.RankedSlot;
import io.lark.rank.RankingResult;
import io.lark.rank.RankingContext;
import io.lark.rank.SlotAdmission;
import io.lark.rank.SequentialRanker;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Sequential ranking demo.
 *
 * <p>Initial scores are computed columnar for the whole goods list. The list is
 * then consumed slot by slot: several admission rules run first, the remaining
 * candidates execute a small score subgraph again, radix sort picks the next
 * best item, and the loop continues.
 *
 * <p>Run with {@code ./gradlew :examples:runRank}.
 */
public final class SequentialRankDemo {

    private static final int SLOTS = 15;
    private static final int CANDIDATES = 40;

    private SequentialRankDemo() {
    }

    public static void main(String[] args) {
        List<GoodsDO> items = sampleGoods(CANDIDATES);
        ScoreModule scoreModule = new ScoreModule();

        // 1. Whole-list columnar pass: base score.
        double[] baseScores = scoreModule.baseScores(
                items.stream().map(item -> new ScoreRow(
                        item.ctr(), item.cvr(), item.gmv(), item.quality(),
                        item.price(), 0.0, 0.0, 0.0)).toList());

        List<ScoredGoods> ordered = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            ordered.add(new ScoredGoods(items.get(i), baseScores[i]));
        }
        // The input list is already ordered by the columnar base score.
        ordered.sort(Comparator.comparingDouble(ScoredGoods::baseScore).reversed());

        Print.section("columnar base order (first 10)");
        Print.rows(ordered.stream().limit(10).toList());

        // 2. Slot-by-slot business ranking.
        RankContext rankContext = new RankContext(900.0);
        SequentialRanker<ScoredGoods, RankContext> ranker =
                SequentialRanker.<ScoredGoods, RankContext>builder(SLOTS)
                        // Slot rule: adult goods cannot take the first 3 slots.
                        .admission((ctx, scored) ->
                                ctx.slot() >= 3 || !scored.goods().adult())
                        // Stateful sliding window: same category at most once per 3 slots.
                        .admission(new SlidingCategoryLimiter(3, 1))
                        // Quota rule: same brand at most 2 items.
                        .admission((ctx, scored) ->
                                ctx.selectedCount(other -> other.goods().brand()
                                        .equals(scored.goods().brand())) < 4)
                        // Business context rule.
                        .admission((ctx, scored) ->
                                scored.goods().price()
                                        <= ctx.businessContext().budget())
                        // Re-run a small score subgraph for the admitted indexes.
                        .scorer((ctx, indices, count) -> scoreModule.updatedScores(
                                scoreRows(ctx, indices, count)))
                        .maxDuration(java.time.Duration.ofMillis(200))
                        .build();

        RankingResult<ScoredGoods> result =
                ranker.rankWithStats(ordered, rankContext);

        Print.section("greedy sequence");
        Print.selected(result.slots());
        Print.constraints(result.slots());
        Print.stats(result);

        BeamSearchRanker<ScoredGoods, RankContext> beam =
                BeamSearchRanker.<ScoredGoods, RankContext>builder(SLOTS, 8)
                        .admission((ctx, scored) ->
                                ctx.slot() >= 3 || !scored.goods().adult())
                        .admission(new SlidingCategoryLimiter(3, 1))
                        .admission((ctx, scored) ->
                                ctx.selectedCount(other -> other.goods().brand()
                                        .equals(scored.goods().brand())) < 4)
                        .admission((ctx, scored) ->
                                scored.goods().price()
                                        <= ctx.businessContext().budget())
                        .scorer((ctx, indices, count) -> scoreModule.updatedScores(
                                scoreRows(ctx, indices, count)))
                        .maxDuration(java.time.Duration.ofMillis(200))
                        .build();

        RankingResult<ScoredGoods> beamResult =
                beam.rankWithStats(ordered, rankContext);

        Print.section("beam search sequence (width=8)");
        Print.selected(beamResult.slots());
        Print.constraints(beamResult.slots());
        Print.stats(beamResult);
    }

    private static List<ScoreRow> scoreRows(
            RankingContext<ScoredGoods, RankContext> ctx,
            int[] indices, int count) {
        List<ScoreRow> rows = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ScoredGoods scored = ctx.candidate(indices[i]);
            GoodsDO goods = scored.goods();
            int sameCategory = ctx.selectedCount(other ->
                    other.goods().category().equals(goods.category()));
            int sameBrand = ctx.selectedCount(other ->
                    other.goods().brand().equals(goods.brand()));
            rows.add(new ScoreRow(
                    goods.ctr(), goods.cvr(), goods.gmv(), goods.quality(),
                    goods.price(), scored.baseScore(),
                    sameCategory * 1.5, sameBrand * 0.8));
        }
        return rows;
    }

    private static List<GoodsDO> sampleGoods(int count) {
        String[] categories = {"shoes", "bag", "phone", "beauty", "food"};
        String[] brands = {"A", "B", "C", "D"};
        Random random = new Random(42);
        List<GoodsDO> goods = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            goods.add(new GoodsDO(
                    i + 1,
                    categories[i % categories.length],
                    brands[i % brands.length],
                    i % 9 == 0,
                    50 + random.nextInt(950),
                    0.01 + random.nextDouble() * 0.09,
                    0.02 + random.nextDouble() * 0.18,
                    10 + random.nextDouble() * 990,
                    1 + random.nextDouble() * 5));
        }
        return goods;
    }

    record GoodsDO(long id, String category, String brand, boolean adult,
                   int price, double ctr, double cvr, double gmv,
                   double quality) {
    }

    record ScoredGoods(GoodsDO goods, double baseScore) {
    }

    record RankContext(double budget) {
    }

    /**
     * Stateful limiter: a category may appear at most {@code maxPerWindow}
     * times in the last {@code window} slots.
     *
     * <p>The engine creates a fresh instance per {@code rank} execution through
     * {@link #newSession()} and feeds every filled slot back through
     * {@link #onSelected}. State therefore survives all re-sorts inside one run
     * but never leaks into the next request.
     */
    static final class SlidingCategoryLimiter
            implements SlotAdmission<ScoredGoods, RankContext> {

        private final int window;
        private final int maxPerWindow;
        private final Deque<String> recentCategories = new ArrayDeque<>();

        SlidingCategoryLimiter(int window, int maxPerWindow) {
            if (window <= 0 || maxPerWindow <= 0) {
                throw new IllegalArgumentException(
                        "window and maxPerWindow must be positive");
            }
            this.window = window;
            this.maxPerWindow = maxPerWindow;
        }

        @Override
        public SlotAdmission<ScoredGoods, RankContext> newSession() {
            return new SlidingCategoryLimiter(window, maxPerWindow);
        }

        @Override
        public boolean admit(RankingContext<ScoredGoods, RankContext> context,
                             ScoredGoods candidate) {
            String category = candidate.goods().category();
            int count = 0;
            for (String recent : recentCategories) {
                if (recent.equals(category)) {
                    count++;
                }
            }
            return count < maxPerWindow;
        }

        @Override
        public void onSelected(RankingContext<ScoredGoods, RankContext> context,
                               RankedSlot<ScoredGoods> slot) {
            recentCategories.addLast(slot.item().goods().category());
            while (recentCategories.size() > window) {
                recentCategories.removeFirst();
            }
        }
    }

    record ScoreRow(double ctr, double cvr, double gmv, double quality,
                    double price, double base, double categoryPenalty,
                    double brandPenalty) {
    }

    /** Small column subgraph executed for the whole list and for each slot. */
    static final class ScoreModule extends ColumnModule<Object> {

        static final Column<Double> CTR = new Column<>("ctr", DType.FLOAT64);
        static final Column<Double> CVR = new Column<>("cvr", DType.FLOAT64);
        static final Column<Double> GMV = new Column<>("gmv", DType.FLOAT64);
        static final Column<Double> QUALITY =
                new Column<>("quality", DType.FLOAT64);
        static final Column<Double> PRICE = new Column<>("price", DType.FLOAT64);
        static final Column<Double> BASE = new Column<>("base", DType.FLOAT64);
        static final Column<Double> CATEGORY_PENALTY =
                new Column<>("category_penalty", DType.FLOAT64);
        static final Column<Double> BRAND_PENALTY =
                new Column<>("brand_penalty", DType.FLOAT64);
        static final Column<Double> BASE_SCORE =
                new Column<>("base_score", DType.FLOAT64);
        static final Column<Double> UPDATED_SCORE =
                new Column<>("updated_score", DType.FLOAT64);

        ColumnStream<Double> ctr;
        ColumnStream<Double> cvr;
        ColumnStream<Double> gmv;
        ColumnStream<Double> quality;
        ColumnStream<Double> price;
        ColumnStream<Double> base;
        ColumnStream<Double> categoryPenalty;
        ColumnStream<Double> brandPenalty;
        ColumnStream<Double> baseScore;
        ColumnStream<Double> updatedScore;

        private FeedMapping<ScoreRow> mapping;
        private ColumnPipeline pipeline;

        @Override
        protected void build() {
            ctr = feed(CTR);
            cvr = feed(CVR);
            gmv = feed(GMV);
            quality = feed(QUALITY);
            price = feed(PRICE);
            base = feed(BASE);
            categoryPenalty = feed(CATEGORY_PENALTY);
            brandPenalty = feed(BRAND_PENALTY);

            mapping = FeedMapping.<ScoreRow>builder()
                    .column(ctr, ScoreRow::ctr)
                    .column(cvr, ScoreRow::cvr)
                    .column(gmv, ScoreRow::gmv)
                    .column(quality, ScoreRow::quality)
                    .column(price, ScoreRow::price)
                    .column(base, ScoreRow::base)
                    .column(categoryPenalty, ScoreRow::categoryPenalty)
                    .column(brandPenalty, ScoreRow::brandPenalty)
                    .build();

            baseScore = formula(BASE_SCORE, F.sum(
                    F.weighted(F.col(ctr), "rank.ctr_w", 100.0),
                    F.weighted(F.col(cvr), "rank.cvr_w", 50.0),
                    F.weighted(F.col(gmv), "rank.gmv_w", 0.01),
                    F.weighted(F.col(quality), "rank.quality_w", 2.0),
                    F.neg(F.weighted(F.col(price), "rank.price_w", 0.001))));

            updatedScore = formula(UPDATED_SCORE, F.sum(
                    F.col(base),
                    F.neg(F.col(categoryPenalty)),
                    F.neg(F.col(brandPenalty))));

            output(baseScore, updatedScore);
        }

        double[] baseScores(List<ScoreRow> rows) {
            return scores(rows, false);
        }

        double[] updatedScores(List<ScoreRow> rows) {
            return scores(rows, true);
        }

        private double[] scores(List<ScoreRow> rows, boolean updated) {
            if (pipeline == null) {
                pipeline = new ColumnPipeline(1);
                pipeline.add(this);
            }
            pipeline.execute(mapping.map(rows));
            Tensor tensor = pipeline.fetchTensor(
                    updated ? updatedScore : baseScore);
            double[] values = new double[tensor.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = tensor.getDouble(i);
            }
            return values;
        }
    }

    static final class Print {

        private Print() {
        }

        static void section(String title) {
            System.out.printf("%n=== %s ===%n", title);
        }

        static void rows(List<ScoredGoods> goods) {
            System.out.printf("%6s %10s %8s %6s %6s %10s%n",
                    "goods", "category", "brand", "adult", "price", "base");
            for (ScoredGoods scored : goods) {
                GoodsDO item = scored.goods();
                System.out.printf("%6d %10s %8s %6s %6d %10.3f%n",
                        item.id(), item.category(), item.brand(),
                        item.adult(), item.price(), scored.baseScore());
            }
        }

        static void selected(List<RankedSlot<ScoredGoods>> sequence) {
            System.out.printf("%5s %6s %10s %8s %6s %10s %10s%n",
                    "slot", "goods", "category", "brand", "price",
                    "base", "updated");
            for (RankedSlot<ScoredGoods> slot : sequence) {
                GoodsDO item = slot.item().goods();
                System.out.printf("%5d %6d %10s %8s %6d %10.3f %10.3f%n",
                        slot.slot() + 1, item.id(), item.category(),
                        item.brand(), item.price(), slot.item().baseScore(),
                        slot.score());
            }
        }

        static void constraints(List<RankedSlot<ScoredGoods>> sequence) {
            boolean adultRule = sequence.stream()
                    .filter(slot -> slot.slot() < 3)
                    .noneMatch(slot -> slot.item().goods().adult());
            int maxBrand = 0;
            for (String brand : List.of("A", "B", "C", "D")) {
                int count = (int) sequence.stream()
                        .filter(slot -> slot.item().goods().brand().equals(brand))
                        .count();
                maxBrand = Math.max(maxBrand, count);
            }
            System.out.printf("%nchecks: adult-first3=%s, max-brand=%d, slots=%d%n",
                    adultRule, maxBrand, sequence.size());
        }

        static void stats(RankingResult<ScoredGoods> result) {
            System.out.printf("rank stats: candidates=%d, admitted=%d, "
                            + "score-calls=%d, sorts=%d, elapsed=%.3fms, "
                            + "timedOut=%s%n",
                    result.stats().candidates(),
                    result.stats().admittedTotal(),
                    result.stats().scoreCalls(),
                    result.stats().sortCalls(),
                    result.stats().elapsedNanos() / 1_000_000.0,
                    result.stats().timedOut());
        }
    }
}
