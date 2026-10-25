// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.data;

import io.lark.column.Cell;
import io.lark.column.Row;

import java.util.List;

/** Demo transaction source; in a real service this would be a repository client. */
public final class TransactionRepository {

    private TransactionRepository() {
    }

    public static List<Row> transactions() {
        // user_id, amount_fen, txn_count_24h, chargeback_count, merchant_id
        return List.of(
                Row.of(Cell.of(1001L), Cell.of(120_000L), Cell.of(4L),
                        Cell.of(0L), Cell.of(7L)),
                Row.of(Cell.of(1002L), Cell.of(850_000L), Cell.of(12L),
                        Cell.of(1L), Cell.of(9L)),
                Row.of(Cell.of(1003L), Cell.of(5_000L), Cell.of(1L),
                        Cell.of(0L), Cell.of(7L)),
                Row.of(Cell.of(1004L), Cell.of(2_500_000L), Cell.of(30L),
                        Cell.of(2L), Cell.of(3L)),
                Row.of(Cell.of(1005L), Cell.of(45_000L), Cell.of(3L),
                        Cell.of(0L), Cell.of(7L)),
                Row.of(Cell.of(1006L), Cell.of(1_500_000L), Cell.of(20L),
                        Cell.of(3L), Cell.of(9L)));
    }
}
