// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.examples.search.data.GoodsDO;
import io.lark.examples.search.data.SearchContext;
import io.lark.examples.search.data.SearchFeed;
import io.lark.examples.search.data.SearchRepository;
import io.lark.examples.search.data.SearchRequest;
import io.lark.examples.search.module.BuyAgainModule;
import io.lark.examples.search.module.CouponRoiModule;
import io.lark.examples.search.module.CtCvrModule;
import io.lark.examples.search.module.CtrModule;
import io.lark.examples.search.module.CvrModule;
import io.lark.examples.search.module.DelayModule;
import io.lark.examples.search.module.GmvModule;
import io.lark.examples.search.module.RebuySameLossModule;
import io.lark.examples.search.module.AdjustedScoreModule;
import io.lark.examples.search.module.RankingScoreModule;
import io.lark.examples.search.module.RetargetAdModule;
import io.lark.examples.search.module.TopKModule;
import io.lark.examples.search.module.SearchInputModule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchDemoTest {

    @Test
    void computesScalarAndShapedSearchMetrics() {
        GoodsDO firstGoods = SearchRepository.sampleRequest().goods().goods().get(0);
        assertEquals(0.05, firstGoods.ctr(), 1e-9);
        assertEquals(0.10, firstGoods.cvr(), 1e-9);
        assertEquals(500.0, firstGoods.gmv(), 1e-9);
        assertEquals(10.0, firstGoods.couponCost(), 1e-9);

        SearchInputModule inputs = new SearchInputModule();
        CtrModule ctr = new CtrModule();
        CvrModule cvr = new CvrModule();
        CtCvrModule ctcvr = new CtCvrModule(ctr, cvr);
        GmvModule gmv = new GmvModule();
        CouponRoiModule coupon = new CouponRoiModule(gmv);
        BuyAgainModule buyAgain = new BuyAgainModule();
        RebuySameLossModule rebuy = new RebuySameLossModule(buyAgain);
        RetargetAdModule retarget = new RetargetAdModule();
        DelayModule delay = new DelayModule();

        try (ColumnPipeline pipeline = new ColumnPipeline(3)) {
            pipeline.addAll(inputs, ctr, cvr, ctcvr, gmv, coupon,
                            buyAgain, rebuy, retarget, delay)
                    .compile()
                    .feed(SearchFeed.sample(inputs))
                    .compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    ctr.ctr(), cvr.cvr(), ctcvr.ctcvr(),
                    buyAgain.buyagain(), rebuy.rebuySameLoss(),
                    coupon.couponRoi(), gmv.gmv()));

            assertEquals(5, rows.size());
            assertEquals(0.05, rows.get(0).get(0).asDouble(), 1e-9);
            assertEquals(0.10, rows.get(0).get(1).asDouble(), 1e-9);
            assertEquals(0.005, rows.get(0).get(2).asDouble(), 1e-9);
            assertEquals(0.20, rows.get(0).get(3).asDouble(), 1e-9);
            assertEquals(0.80, rows.get(0).get(4).asDouble(), 1e-9);
            assertEquals(50.0, rows.get(0).get(5).asDouble(), 1e-9);
            assertEquals(500.0, rows.get(0).get(6).asDouble(), 1e-9);

            // Zero coupon cost / zero retarget cost must degrade to zero.
            assertEquals(0.0, rows.get(2).get(5).asDouble(), 1e-9);

            Tensor retargetTensor = pipeline.fetchTensor(retarget.retargetAd());
            assertEquals(TensorShape.of(5, 3), retargetTensor.shape());
            assertEquals(0.40, retargetTensor.getDouble(0), 1e-9);
            assertEquals(0.004, retargetTensor.getDouble(1), 1e-9);
            assertEquals(250.0, retargetTensor.getDouble(2), 1e-9);
            assertEquals(0.0, retargetTensor.getDouble(2 * 3 + 2), 1e-9);

            Tensor delayTensor = pipeline.fetchTensor(delay.delay());
            assertEquals(TensorShape.of(5, 2, 2), delayTensor.shape());
            assertEquals(0.80, delayTensor.getDouble(0), 1e-9);
            assertEquals(0.20, delayTensor.getDouble(1), 1e-9);
            assertEquals(0.90, delayTensor.getDouble(2), 1e-9);
            assertEquals(0.10, delayTensor.getDouble(3), 1e-9);
            assertEquals(1.0, delayTensor.getDouble(2 * 4), 1e-9);
        }
    }

    @Test
    void selectsTopKPerPvFromSimulatedCandidates() {
        SearchContext first = new SearchContext(101, 7, "red shoes", 30);
        SearchContext second = new SearchContext(202, 7, "blue shoes", 30);
        SearchRequest firstRequest = SearchRepository.simulate(first, 1_000);
        SearchRequest secondRequest = SearchRepository.simulate(second, 1_000);

        List<Row> feedRows = new ArrayList<>();
        feedRows.addAll(SearchRepository.rows(firstRequest));
        feedRows.addAll(SearchRepository.rows(secondRequest));

        SearchInputModule inputs = new SearchInputModule();
        CtrModule ctr = new CtrModule();
        CvrModule cvr = new CvrModule();
        GmvModule gmv = new GmvModule();
        CouponRoiModule coupon = new CouponRoiModule(gmv);
        BuyAgainModule buyAgain = new BuyAgainModule();
        RebuySameLossModule rebuy = new RebuySameLossModule(buyAgain);
        RankingScoreModule score = new RankingScoreModule(
                ctr, cvr, gmv, coupon, rebuy);
        AdjustedScoreModule adjusted = new AdjustedScoreModule(score);
        TopKModule topK = new TopKModule(adjusted,
                new SearchRankingOptions(first.topK()));

        try (ColumnPipeline pipeline = new ColumnPipeline(4)) {
            pipeline.addAll(inputs, ctr, cvr, gmv, coupon, buyAgain, rebuy,
                            score, adjusted, topK)
                    .compile()
                    .feed(SearchFeed.of(inputs, feedRows))
                    .compute();

            List<Row> ranked = pipeline.fetch(FetchStream.of(
                    inputs.pvId(), inputs.goodsId(),
                    adjusted.adjustedScore(), topK.selected()));

            assertEquals(2_000, ranked.size());
            long selected = ranked.stream()
                    .filter(row -> row.get(3).asLong() == 1L)
                    .count();
            assertEquals(60, selected);
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
    void serviceReturnsTypedRankedResult() {
        SearchContext context = SearchRepository.sampleContext();
        SearchRequest request = SearchRepository.simulate(context, 1_000);

        SearchRankingService service = SearchRankingService.create(
                context.topK(), 4);
        SearchResult result = service.search(request);

        assertEquals(context, result.context());
        assertEquals(1_000, result.candidateCount());
        assertEquals(30, result.ranked().size());
        for (int i = 0; i < result.ranked().size(); i++) {
            RankedGoods goods = result.ranked().get(i);
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
