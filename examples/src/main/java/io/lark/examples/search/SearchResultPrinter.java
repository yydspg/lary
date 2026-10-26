// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.data.Row;
import io.lark.column.tensor.Tensor;

import java.util.List;

/** Prints search metric rows and the final ranked goods list. */
public final class SearchResultPrinter {

    private SearchResultPrinter() {
    }

    public static void print(SearchResult result) {
        System.out.printf("=== search ranking (pv=%d, user=%d, query=%s, "
                        + "candidates=%d, topK=%d, experiment=%s) ===%n",
                result.context().pvId(), result.context().userId(),
                result.context().query(), result.candidateCount(),
                result.context().topK(),
                result.context().experiment().variants());
        printRanking(result.ranked());

        Tensor retarget = result.retargetAd();
        System.out.printf("retarget_ad shape=%s size=%d (first rows shown)%n",
                retarget.shape(), retarget.size());
        double[] retargetValues = retarget.doubleData();
        for (int i = 0; i < Math.min(3, retarget.shape().dim(0)); i++) {
            System.out.printf("  goods %d: click_share=%.4f cost_share=%.4f roi=%.4f%n",
                    i + 1, retargetValues[i * 3], retargetValues[i * 3 + 1],
                    retargetValues[i * 3 + 2]);
        }

        Tensor delay = result.delay();
        System.out.printf("delay shape=%s size=%d (first rows shown)%n",
                delay.shape(), delay.size());
        double[] delayValues = delay.doubleData();
        for (int i = 0; i < Math.min(3, delay.shape().dim(0)); i++) {
            int base = i * 4;
            System.out.printf("  goods %d: order-on-time=%.4f order-delayed=%.4f "
                            + "gmv-on-time=%.4f gmv-delayed=%.4f%n",
                    i + 1, delayValues[base], delayValues[base + 1],
                    delayValues[base + 2], delayValues[base + 3]);
        }

        System.out.print(result.stats().summary());
    }

    public static void printTable(List<Row> rows) {
        System.out.printf("%8s %8s %8s %10s %14s %10s %10s%n",
                "ctr", "cvr", "ctcvr", "buyagain", "rebuy_same_loss",
                "couponroi", "gmv");
        for (Row row : rows) {
            System.out.printf("%8.4f %8.4f %8.4f %10.4f %14.4f %10.4f %10.2f%n",
                    row.get(0).asDouble(),
                    row.get(1).asDouble(),
                    row.get(2).asDouble(),
                    row.get(3).asDouble(),
                    row.get(4).asDouble(),
                    row.get(5).asDouble(),
                    row.get(6).asDouble());
        }
    }

    public static void printRanking(List<RankedGoods> ranked) {
        System.out.printf("%5s %10s %16s%n", "rank", "goods_id", "adjusted_score");
        for (RankedGoods goods : ranked) {
            System.out.printf("%5d %10d %16.4f%n",
                    goods.rank(), goods.goodsId(), goods.adjustedScore());
        }
    }
}
