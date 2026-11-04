// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.context.ExperimentContext;
import io.lark.column.tensor.Tensor;
import io.lark.examples.search.model.GoodsDO;
import io.lark.examples.search.model.GoodsList;
import io.lark.examples.search.model.SearchContext;
import io.lark.examples.search.model.SearchRequest;
import io.lark.examples.search.model.SearchRow;
import io.lark.examples.search.module.SearchModule;
import io.lark.examples.search.result.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Search ranking demo CLI, sample data and output. The request model lives in
 * {@code search.model}, the graph in {@code search.module}, and the typed
 * result in {@link SearchResult}.
 */
public final class SearchDemo {

    private static final int CANDIDATES = 1_000;
    private static final int WORKERS = 4;

    private SearchDemo() {
    }

    public static void main(String[] args) {
        SearchContext context = sampleContext();
        SearchRequest request = simulate(context, CANDIDATES);
        SearchResult result = new SearchModule(context.topK(), WORKERS)
                .search(request);
        print(result);
    }

    /** Flatten one request into typed per-row feeds. */
    public static List<SearchRow> rows(SearchRequest request) {
        List<SearchRow> rows = new ArrayList<>(request.goods().size());
        for (GoodsDO goods : request.goods().goods()) {
            rows.add(new SearchRow(request.context().pvId(), goods));
        }
        return rows;
    }

    // ---- deterministic sample data --------------------------------------

    public static SearchContext sampleContext() {
        return new SearchContext(1_001L, 42L, "red shoes", 30,
                sampleExperiment());
    }

    public static ExperimentContext sampleExperiment() {
        return ExperimentContext.builder()
                .variant("rerank", "coupon_boost")
                .param("rerank.ctr_w", 100.0)
                .param("rerank.cvr_w", 50.0)
                .param("rerank.gmv_w", 0.01)
                .param("rerank.coupon_roi_w", 0.5)
                .param("rerank.rebuy_w", 0.5)
                .param("rerank.coupon_boost_w", 0.02)
                .build();
    }

    public static SearchRequest sampleRequest() {
        SearchContext context = sampleContext();
        return new SearchRequest(context, new GoodsList(List.of(
                goods(1, 1_000, 50, 5, 50_000, 1_000, 1, 1, 5_000, 20, 200, 0.80, 10),
                goods(2, 2_000, 100, 20, 300_000, 5_000, 4, 8, 60_000, 45, 800, 0.90, 20),
                goods(3, 500, 10, 1, 8_000, 0, 0, 0, 0, 5, 0, 1.00, 30),
                goods(4, 1_500, 120, 30, 600_000, 8_000, 10, 15, 90_000, 60, 1_200, 0.85, 40),
                goods(5, 800, 40, 8, 120_000, 2_000, 2, 4, 15_000, 18, 300, 0.95, 50))));
    }

    public static SearchRequest simulate(SearchContext context, int goodsCount) {
        if (goodsCount <= 0) {
            throw new IllegalArgumentException("goodsCount must be positive");
        }
        Random random = new Random(context.pvId() * 1_000_003L + context.userId());
        List<GoodsDO> goods = new ArrayList<>(goodsCount);
        for (int i = 0; i < goodsCount; i++) {
            long impressions = 100 + random.nextInt(4_901);
            long clicks = Math.min(impressions,
                    Math.max(1, impressions * (2 + random.nextInt(8)) / 100));
            long orders = clicks == 0 ? 0
                    : Math.min(clicks,
                            Math.max(1, clicks * (1 + random.nextInt(5)) / 100));
            long gmvFen = orders * (500 + random.nextInt(5_000));
            long couponCostFen = orders == 0 || random.nextInt(5) == 0
                    ? 0 : orders * (50 + random.nextInt(400));
            long buyAgainOrders = orders == 0 ? 0
                    : random.nextInt((int) orders + 1);
            long delayedOrders = orders == 0 ? 0
                    : random.nextInt((int) orders + 1);
            long delayedGmvFen = orders == 0 ? 0
                    : gmvFen * delayedOrders / orders;
            long retargetClicks = clicks == 0 ? 0
                    : random.nextInt((int) clicks + 1);
            long retargetCostFen = retargetClicks * (10 + random.nextInt(200));
            double perturbation = 0.80 + random.nextDouble() * 0.30;
            double originalScore = random.nextDouble() * 100.0;
            goods.add(goods(i + 1, impressions, clicks, orders, gmvFen,
                    couponCostFen, buyAgainOrders, delayedOrders,
                    delayedGmvFen, retargetClicks, retargetCostFen,
                    perturbation, originalScore));
        }
        return new SearchRequest(context, new GoodsList(goods));
    }

    // ---- output ---------------------------------------------------------

    public static void print(SearchResult result) {
        SearchContext context = result.context();
        System.out.printf("=== search ranking (pv=%d, user=%d, query=%s, "
                        + "candidates=%d, topK=%d, experiment=%s) ===%n",
                context.pvId(), context.userId(), context.query(),
                result.candidateCount(), context.topK(),
                context.experiment().variants());

        System.out.printf("%5s %10s %16s%n",
                "rank", "goods_id", "adjusted_score");
        for (SearchResult.RankedGoods goods : result.ranked()) {
            System.out.printf("%5d %10d %16.4f%n",
                    goods.rank(), goods.goodsId(), goods.adjustedScore());
        }

        Tensor retarget = result.retargetAd();
        System.out.printf("retarget_ad shape=%s size=%d (first rows shown)%n",
                retarget.shape(), retarget.size());
        double[] retargetValues = retarget.doubleData();
        for (int i = 0; i < Math.min(3, retarget.shape().dim(0)); i++) {
            System.out.printf(
                    "  goods %d: click_share=%.4f cost_share=%.4f roi=%.4f%n",
                    i + 1, retargetValues[i * 3], retargetValues[i * 3 + 1],
                    retargetValues[i * 3 + 2]);
        }

        Tensor delay = result.delay();
        System.out.printf("delay shape=%s size=%d (first rows shown)%n",
                delay.shape(), delay.size());
        double[] delayValues = delay.doubleData();
        for (int i = 0; i < Math.min(3, delay.shape().dim(0)); i++) {
            int base = i * 4;
            System.out.printf(
                    "  goods %d: order-on-time=%.4f order-delayed=%.4f "
                            + "gmv-on-time=%.4f gmv-delayed=%.4f%n",
                    i + 1, delayValues[base], delayValues[base + 1],
                    delayValues[base + 2], delayValues[base + 3]);
        }

        System.out.print(result.stats().summary());
    }

    private static GoodsDO goods(long id, long impressions, long clicks,
                                 long orders, long gmvFen, long couponCostFen,
                                 long buyAgainOrders, long delayedOrders,
                                 long delayedGmvFen, long retargetClicks,
                                 long retargetCostFen, double perturbation,
                                 double originalScore) {
        return new GoodsDO(id, impressions, clicks, orders, gmvFen,
                couponCostFen, buyAgainOrders, delayedOrders, delayedGmvFen,
                retargetClicks, retargetCostFen, perturbation, originalScore);
    }
}
