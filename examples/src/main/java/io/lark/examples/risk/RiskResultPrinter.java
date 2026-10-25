// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk;

import io.lark.column.Row;

import java.util.List;

/** Formats scored transactions for the console demo. */
public final class RiskResultPrinter {

    private RiskResultPrinter() {
    }

    public static void printTable(List<Row> rows) {
        System.out.println("=== payment risk engine ===");
        System.out.printf("%-8s %12s %12s %8s %10s %6s%n",
                "user", "amount_yuan", "risk_score", "level", "approved", "tier");
        for (Row row : rows) {
            System.out.printf("%-8d %12.2f %12.2f %8d %10d %6d%n",
                    row.get(0).asLong(),
                    row.get(1).asDouble(),
                    row.get(2).asDouble(),
                    row.get(3).asLong(),
                    row.get(4).asLong(),
                    row.get(5).asLong());
        }
    }
}
