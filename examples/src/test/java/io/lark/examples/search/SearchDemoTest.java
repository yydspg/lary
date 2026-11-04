// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.column.context.ExperimentContext;
import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchDemoTest {

    @Test
    void computesScalarAndShapedSearchMetrics() {
        SearchDemo.GoodsDO firstGoods =
                SearchDemo.sampleRequest().goods().goods().get(0);
        assertEquals(0.05, firstGoods.ctr(), 1e-9);
        assertEquals(0.10, firstGoods.cvr(), 1e-9);
        assertEquals(500.0, firstGoods.gmv(), 1e-9);
        assertEquals(10.0, firstGoods.couponCost(), 1e-9);

        SearchModule module = new SearchModule(30, 3);
        try (ColumnPipeline pipeline = new ColumnPipeline(3)) {
            pipeline.add(module)
                    .compile()
                    .feed(module.feedStream(SearchDemo.sampleRequest()))
                    .compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    module.ctr, module.cvr, module.ctcvr,
                    module.buyAgain, module.rebuyLoss,
                    module.couponRoi, module.gmv));

            assertEquals(5, rows.size());
            assertEquals(0.05, rows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(0.10, rows.get(0).get(1).asDouble(), 1e-9);
            assertEquals(0.005, rows.get(0).get(2).asDouble(), 1e-9);
            assertEquals(0.20, rows.get(0).get(3).asDouble(), 1e-9);
            assertEquals(0.80, rows.get(0).get(4).asDouble(), 1e-9);
            assertEquals(50.0, rows.get(0).get(5).asDouble(), 1e-9);
            assertEquals(500.0, rows.get(0).get(6).asDouble(), 1e-9);
            assertEquals(0.0, rows.get(2).get(5).asDouble(), 1e-9);

            Tensor retarget = pipeline.fetchTensor(module.retargetAd);
            assertEquals(TensorShape.of(5, 3), retarget.shape());
            assertEquals(0.40, retarget.getDouble(0), 1e-9);
            assertEquals(0.004, retarget.getDouble(1), 1e-9);
            assertEquals(250.0, retarget.getDouble(2), 1e-9);
            assertEquals(0.0, retarget.getDouble(2 * 3 + 2), 1e-9);

            Tensor delay = pipeline.fetchTensor(module.delay);
            assertEquals(TensorShape.of(5, 2, 2), delay.shape());
            assertEquals(0.80, delay.getDouble(0), 1e-9);
            assertEquals(0.20, delay.getDouble(1), 1e-9);
            assertEquals(0.90, delay.getDouble(2), 1e-9);
            assertEquals(0.10, delay.getDouble(3), 1e-9);
            assertEquals(1.0, delay.getDouble(2 * 4), 1e-9);
        }
    }

    @Test
    void selectsTopKPerPvFromSimulatedCandidates() {
        SearchDemo.SearchContext first =
                new SearchDemo.SearchContext(101, 7, "red shoes", 30);
        SearchDemo.SearchContext second =
                new SearchDemo.SearchContext(202, 7, "blue shoes", 30);
        SearchDemo.SearchRequest firstRequest = SearchDemo.simulate(first, 1_000);
        SearchDemo.SearchRequest secondRequest = SearchDemo.simulate(second, 1_000);

        List<Row> feedRows = new ArrayList<>();
        feedRows.addAll(SearchDemo.rows(firstRequest));
        feedRows.addAll(SearchDemo.rows(secondRequest));

        SearchModule module = new SearchModule(30, 4);
        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
            pipeline.add(module)
                    .compile()
                    .feed(module.feedStream(feedRows))
                    .compute();

            List<Row> ranked = pipeline.fetch(FetchStream.of(
                    module.pvId, module.goodsId,
                    module.adjustedScore, module.selected));

            assertEquals(2_000, ranked.size());
            assertEquals(60, ranked.stream()
                    .filter(row -> row.get(3).asLong() == 1L)
                    .count());
            for (long pv : List.of(first.pvId(), second.pvId())) {
                List<Row> pvRows = ranked.stream()
                        .filter(row -> row.get(0).asLong() == pv)
                        .toList();
                assertEquals(1_000, pvRows.size());
                assertEquals(30, pvRows.stream()
                        .filter(row -> row.get(3).asLong() == 1L)
                        .count());

                double minSelected = pvRows.stream()
                        .filter(row -> row.get(3).asLong() == 1L)
                        .mapToDouble(row -> row.get(2).asDouble())
                        .min().orElseThrow();
                double maxUnselected = pvRows.stream()
                        .filter(row -> row.get(3).asLong() == 0L)
                        .mapToDouble(row -> row.get(2).asDouble())
                        .max().orElseThrow();
                assertTrue(minSelected >= maxUnselected);
            }

            List<Row> top = ranked.stream()
                    .filter(row -> row.get(3).asLong() == 1L)
                    .sorted(Comparator.comparingDouble(
                            (Row row) -> row.get(2).asDouble()).reversed())
                    .limit(3)
                    .toList();
            assertTrue(top.get(0).get(2).asDouble()
                    >= top.get(1).get(2).asDouble());
        }
    }

    @Test
    void experimentVariantChangesRerankFormula() {
        SearchModule module = new SearchModule(30, 2);
        ExperimentContext control = ExperimentContext.builder()
                .variant("rerank", "control")
                .build();
        ExperimentContext boost = ExperimentContext.builder()
                .variant("rerank", "coupon_boost")
                .build();

        try (ColumnPipeline pipeline = new ColumnPipeline(2)) {
            pipeline.add(module)
                    .setExperiment(control)
                    .compile()
                    .feed(module.feedStream(SearchDemo.sampleRequest()))
                    .compute();
            double controlScore =
                    pipeline.fetchTensor(module.rankingScore).getDouble(0);

            pipeline.setExperiment(boost).compute();
            double boostScore =
                    pipeline.fetchTensor(module.rankingScore).getDouble(0);

            // Row 0 has GMV 500 and coupon ROI > 0, so coupon_boost adds
            // exactly 500 * 0.02.
            assertEquals(controlScore + 10.0, boostScore, 1e-9);
        }
    }

    @Test
    void serviceReturnsTypedRankedResult() {
        SearchDemo.SearchContext context = SearchDemo.sampleContext();
        SearchDemo.SearchRequest request =
                SearchDemo.simulate(context, 1_000);

        SearchResult result = new SearchModule(context.topK(), 4)
                .search(request);

        assertEquals(context, result.context());
        assertEquals(1_000, result.candidateCount());
        assertEquals(30, result.ranked().size());
        for (int i = 0; i < result.ranked().size(); i++) {
            SearchResult.RankedGoods goods = result.ranked().get(i);
            assertEquals(i + 1, goods.rank());
            if (i > 0) {
                assertTrue(result.ranked().get(i - 1).adjustedScore()
                        >= goods.adjustedScore());
            }
        }
        assertEquals(TensorShape.of(1_000, 3), result.retargetAd().shape());
        assertEquals(TensorShape.of(1_000, 2, 2), result.delay().shape());
    }
}
