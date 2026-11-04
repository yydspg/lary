// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo;

import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.column.context.ColumnRunContext;
import io.lark.column.monitor.ColumnStatsCollector;
import io.lark.column.monitor.RunStats;
import io.lark.column.tensor.Tensor;
import io.lark.dag.DefaultContext;
import io.lark.dag.ExecutionStats;
import io.lark.dag.Executor;
import io.lark.dag.Graph;
import io.lark.dag.GraphBuilder;
import io.lark.dag.IContext;
import io.lark.dag.Node;
import io.lark.dag.NodeDef;
import io.lark.dag.NodeRegistry;
import io.lark.dag.StatsCollector;
import io.lark.examples.search.SearchDemo;
import io.lark.examples.search.SearchModule;
import io.lark.examples.search.SearchSchema;
import io.lark.rank.BeamSearchRanker;
import io.lark.rank.IndexedSlotScorer;
import io.lark.rank.RankedSlot;
import io.lark.rank.Ranker;
import io.lark.rank.RankingResult;
import io.lark.rank.SequentialRanker;
import io.lark.rank.SlotAdmission;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * End-to-end demo: DAG request loading, columnar metric computation with
 * per-PV top-k, then greedy and beam-search slot ranking over the selected
 * goods.
 *
 * <p>The three stages are deliberately independent:
 *
 * <ol>
 *   <li><b>DAG</b> — load the request and request-scoped ranking config in
 *       parallel, then validate the request ({@code Node} layer).</li>
 *   <li><b>Column</b> — compute CTR/CVR/GMV/coupon/retarget/delay columns from
 *       the request rows, apply the experiment-aware formula, and select the
 *       top-k goods per PV.</li>
 *   <li><b>Ranking</b> — run a greedy and a beam-search ranker over the
 *       column-selected candidates with category/brand/budget constraints.</li>
 * </ol>
 *
 * <p>Run with {@code ./gradlew :examples:runFullDemo}. For warmed-up
 * throughput numbers use {@code :examples:runFullBenchmark}.
 */
public final class FullRankingDemo {

    private static final int CANDIDATES = 300;
    private static final int WORKERS = 4;

    private FullRankingDemo() {
    }

    public static void main(String[] args) {
        print(run(CANDIDATES));
    }

    /** Run the demo once and return every intermediate stage. */
    public static Result run(int candidateCount) {
        Prepared prepared = loadRequest(candidateCount);
        ColumnStage column = scoreColumns(prepared.request(), prepared.config());
        List<ScoredGoods> candidates = column.candidates();

        Ranker<ScoredGoods, RankConfig> greedy = greedyRanker(candidates,
                prepared.config());
        Ranker<ScoredGoods, RankConfig> beam = beamRanker(candidates,
                prepared.config());

        RankingResult<ScoredGoods> greedyResult =
                greedy.rankWithStats(candidates, prepared.config());
        RankingResult<ScoredGoods> beamResult =
                beam.rankWithStats(candidates, prepared.config());
        return new Result(prepared, column, greedyResult, beamResult);
    }

    public record Result(Prepared prepared, ColumnStage column,
                         RankingResult<ScoredGoods> greedy,
                         RankingResult<ScoredGoods> beam) {
    }

    public record Prepared(SearchDemo.SearchRequest request, RankConfig config,
                           ExecutionStats dagStats) {
    }

    public record RankConfig(int topK, double budget, int beamWidth) {
    }

    public record ColumnStage(List<ScoredGoods> candidates, int selectedCount,
                              Tensor retargetAd, Tensor delay, RunStats stats) {
    }

    public record ScoredGoods(SearchDemo.GoodsDO goods, double baseScore,
                              int category, int brand, long cost,
                              boolean restricted) {
    }

    // ---- DAG stage -------------------------------------------------------

    private static Prepared loadRequest(int candidateCount) {
        NodeRegistry registry = new NodeRegistry();
        registry.register("load_request",
                () -> new LoadRequestNode(candidateCount));
        registry.register("load_config", LoadConfigNode::new);
        registry.register("validate", ValidateNode::new);

        Graph graph = new GraphBuilder(registry).build(
                new NodeDef("load_request"),
                new NodeDef("load_config"),
                NodeDef.of("validate", "validate", "load_request", "load_config"));

        StatsCollector stats = new StatsCollector();
        DefaultContext context = new DefaultContext();
        try (Executor executor = new Executor(2, 2)) {
            executor.setMonitor(stats);
            executor.execute(graph, context);
        }
        Prepared prepared = context.get("prepared", Prepared.class);
        return new Prepared(prepared.request(), prepared.config(),
                stats.stats());
    }

    static final class LoadRequestNode extends Node {
        private final int candidateCount;

        LoadRequestNode(int candidateCount) {
            this.candidateCount = candidateCount;
        }

        @Override
        public void compute(IContext ctx) {
            ctx.set("request", SearchDemo.simulate(SearchDemo.sampleContext(),
                    candidateCount), SearchDemo.SearchRequest.class);
        }
    }

    static final class LoadConfigNode extends Node {
        @Override
        public void compute(IContext ctx) {
            ctx.set("config", new RankConfig(30, 5_000.0, 8),
                    RankConfig.class);
        }
    }

    static final class ValidateNode extends Node {
        @Override
        public void compute(IContext ctx) {
            SearchDemo.SearchRequest request =
                    ctx.get("request", SearchDemo.SearchRequest.class);
            RankConfig config = ctx.get("config", RankConfig.class);
            if (request.goods().size() == 0) {
                throw new IllegalArgumentException("no candidates");
            }
            ctx.set("prepared", new Prepared(request, config, null),
                    Prepared.class);
        }
    }

    // ---- column stage ----------------------------------------------------

    private static ColumnStage scoreColumns(SearchDemo.SearchRequest request,
                                            RankConfig config) {
        int topK = Math.min(config.topK(), request.goods().size());
        SearchModule module = new SearchModule(topK, WORKERS);
        ColumnStatsCollector stats = new ColumnStatsCollector();
        Tensor adjusted;
        Tensor selected;
        Tensor retarget;
        Tensor delay;
        RunStats runStats;
        try (ColumnPipeline pipeline = new ColumnPipeline(WORKERS)) {
            pipeline.setMonitor(stats);
            pipeline.add(module);

            FetchStream required = FetchStream.of(
                    module.shared(SearchSchema.ADJUSTED_SCORE.doubleColumn()),
                    module.shared(SearchSchema.SELECTED.longColumn()),
                    module.shared(SearchSchema.RETARGET_AD.doubleColumn()),
                    module.shared(SearchSchema.DELAY.doubleColumn()));

            pipeline.setExperiment(request.context().experiment())
                    .executeTensors(module.feedTensorMap(SearchDemo.rows(request)),
                            required,
                            ColumnRunContext.of(request.context()));
            adjusted = pipeline.fetchTensor(
                    SearchSchema.ADJUSTED_SCORE.columnName());
            selected = pipeline.fetchTensor(
                    SearchSchema.SELECTED.columnName());
            retarget = pipeline.fetchTensor(
                    SearchSchema.RETARGET_AD.columnName());
            delay = pipeline.fetchTensor(SearchSchema.DELAY.columnName());
            runStats = pipeline.stats();
        }

        List<SearchDemo.GoodsDO> goods = request.goods().goods();
        List<ScoredGoods> candidates = new ArrayList<>(topK);
        int selectedCount = 0;
        for (int i = 0; i < goods.size(); i++) {
            if (selected.getLong(i) != 1L) {
                continue;
            }
            selectedCount++;
            SearchDemo.GoodsDO item = goods.get(i);
            candidates.add(new ScoredGoods(
                    item,
                    adjusted.getDouble(i),
                    (int) (item.goodsId() % 8),
                    (int) (item.goodsId() % 10),
                    item.couponCostFen(),
                    item.goodsId() % 9 == 0));
        }
        candidates.sort(Comparator
                .comparingDouble(ScoredGoods::baseScore).reversed());
        return new ColumnStage(candidates, selectedCount, retarget, delay,
                runStats);
    }

    // ---- ranking stage ---------------------------------------------------

    private static Ranker<ScoredGoods, RankConfig> greedyRanker(
            List<ScoredGoods> candidates, RankConfig config) {
        return SequentialRanker
                .<ScoredGoods, RankConfig>builder(config.topK())
                .admissions(admissions())
                .scorer(indexedScorer(candidates))
                .maxDuration(Duration.ofMillis(100))
                .build();
    }

    private static Ranker<ScoredGoods, RankConfig> beamRanker(
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

    // ---- output ----------------------------------------------------------

    /** Print one complete demo run. */
    public static void print(Result result) {
        printDag(result.prepared().dagStats());
        printColumn(result.column());
        printRanking("greedy", result.greedy());
        printRanking("beam width=" + result.prepared().config().beamWidth(),
                result.beam());
    }

    private static void printDag(ExecutionStats stats) {
        System.out.println("=== DAG stage ===");
        System.out.print(stats.summary());
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
