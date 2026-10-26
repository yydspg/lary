// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import io.lark.column.data.Cell;
import io.lark.column.data.Row;

import java.util.List;

/** Small deterministic search dataset for the demo and tests. */
public final class SearchRepository {

    private SearchRepository() {
    }

    /**
     * impressions, clicks, orders, gmv_fen, coupon_cost_fen, buy_again_orders,
     * delayed_orders, delayed_gmv_fen, retarget_clicks, retarget_cost_fen.
     */
    public static List<Row> rows() {
        return List.of(
                Row.of(Cell.of(1_000L), Cell.of(50L), Cell.of(5L),
                        Cell.of(50_000L), Cell.of(1_000L), Cell.of(1L),
                        Cell.of(1L), Cell.of(5_000L), Cell.of(20L), Cell.of(200L)),
                Row.of(Cell.of(2_000L), Cell.of(100L), Cell.of(20L),
                        Cell.of(300_000L), Cell.of(5_000L), Cell.of(4L),
                        Cell.of(8L), Cell.of(60_000L), Cell.of(45L), Cell.of(800L)),
                Row.of(Cell.of(500L), Cell.of(10L), Cell.of(1L),
                        Cell.of(8_000L), Cell.of(0L), Cell.of(0L),
                        Cell.of(0L), Cell.of(0L), Cell.of(5L), Cell.of(0L)),
                Row.of(Cell.of(1_500L), Cell.of(120L), Cell.of(30L),
                        Cell.of(600_000L), Cell.of(8_000L), Cell.of(10L),
                        Cell.of(15L), Cell.of(90_000L), Cell.of(60L), Cell.of(1_200L)),
                Row.of(Cell.of(800L), Cell.of(40L), Cell.of(8L),
                        Cell.of(120_000L), Cell.of(2_000L), Cell.of(2L),
                        Cell.of(4L), Cell.of(15_000L), Cell.of(18L), Cell.of(300L)));
    }
}
