// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import io.lark.column.data.Cell;
import io.lark.column.data.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Deterministic simulated search dataset. */
public final class SearchRepository {

    private SearchRepository() {
    }

    public static SearchContext sampleContext() {
        return new SearchContext(1_001L, 42L, "red shoes", 30);
    }

    /** Small hand-authored request for formula tests. */
    public static SearchRequest sampleRequest() {
        SearchContext context = sampleContext();
        return new SearchRequest(context, new GoodsList(List.of(
                goods(1, 1_000, 50, 5, 50_000, 1_000, 1, 1, 5_000, 20, 200,
                        0.80, 10),
                goods(2, 2_000, 100, 20, 300_000, 5_000, 4, 8, 60_000, 45, 800,
                        0.90, 20),
                goods(3, 500, 10, 1, 8_000, 0, 0, 0, 0, 5, 0,
                        1.00, 30),
                goods(4, 1_500, 120, 30, 600_000, 8_000, 10, 15, 90_000, 60, 1_200,
                        0.85, 40),
                goods(5, 800, 40, 8, 120_000, 2_000, 2, 4, 15_000, 18, 300,
                        0.95, 50))));
    }

    /**
     * Simulate one PV with {@code goodsCount} candidate goods. Values are
     * deterministic per PV so tests and demos are repeatable.
     */
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
            long delayedGmvFen = orders == 0 ? 0 : gmvFen * delayedOrders / orders;
            long retargetClicks = clicks == 0 ? 0
                    : random.nextInt((int) clicks + 1);
            long retargetCostFen = retargetClicks * (10 + random.nextInt(200));
            double perturbation = 0.80 + random.nextDouble() * 0.30;
            double originalScore = random.nextDouble() * 100.0;
            goods.add(goods(i + 1, impressions, clicks, orders, gmvFen,
                    couponCostFen, buyAgainOrders, delayedOrders, delayedGmvFen,
                    retargetClicks, retargetCostFen, perturbation, originalScore));
        }
        return new SearchRequest(context, new GoodsList(goods));
    }

    private static GoodsDO goods(long id, long impressions, long clicks, long orders,
                                 long gmvFen, long couponCostFen,
                                 long buyAgainOrders, long delayedOrders,
                                 long delayedGmvFen, long retargetClicks,
                                 long retargetCostFen, double perturbation,
                                 double originalScore) {
        return new GoodsDO(id, impressions, clicks, orders, gmvFen,
                couponCostFen, buyAgainOrders, delayedOrders, delayedGmvFen,
                retargetClicks, retargetCostFen, perturbation, originalScore);
    }

    public static List<Row> sampleRows() {
        return rows(sampleRequest());
    }

    public static List<Row> rows(SearchRequest request) {
        SearchContext context = request.context();
        List<Row> rows = new ArrayList<>(request.goods().size());
        for (GoodsDO goods : request.goods().goods()) {
            rows.add(Row.of(
                    Cell.of(context.pvId()),
                    Cell.of(goods.goodsId()),
                    Cell.of(goods.impressions()),
                    Cell.of(goods.clicks()),
                    Cell.of(goods.orders()),
                    Cell.of(goods.gmvFen()),
                    Cell.of(goods.couponCostFen()),
                    Cell.of(goods.buyAgainOrders()),
                    Cell.of(goods.delayedOrders()),
                    Cell.of(goods.delayedGmvFen()),
                    Cell.of(goods.retargetClicks()),
                    Cell.of(goods.retargetCostFen()),
                    Cell.of(goods.perturbation()),
                    Cell.of(goods.originalScore())));
        }
        return rows;
    }
}
