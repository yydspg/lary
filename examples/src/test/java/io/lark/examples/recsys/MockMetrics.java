// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys;

import io.lark.column.data.Cell;
import io.lark.column.data.Row;

import java.util.List;

/**
 * Small hand-built feed fixture for edge cases:
 *
 * <ul>
 *   <li>normal row</li>
 *   <li>all-zero row (all ratios must be zero)</li>
 *   <li>zero coupon cost and zero repeat orders</li>
 * </ul>
 */
final class MockMetrics {

    private MockMetrics() {
    }

    static List<Row> rows() {
        return List.of(
                // impressions, clicks, orders, gmv_fen, coupon_cost_fen,
                // buy_again_orders
                Row.of(Cell.of(100L), Cell.of(10L), Cell.of(2L),
                        Cell.of(10_000L), Cell.of(100L), Cell.of(1L)),
                Row.of(Cell.of(0L), Cell.of(0L), Cell.of(0L),
                        Cell.of(0L), Cell.of(0L), Cell.of(0L)),
                Row.of(Cell.of(50L), Cell.of(5L), Cell.of(1L),
                        Cell.of(5_000L), Cell.of(0L), Cell.of(0L)));
    }
}
