// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys;

import io.lark.column.Row;

import java.util.List;

/** Prints the per-row metric table. */
public final class MetricResultPrinter {

    private MetricResultPrinter() {
    }

    public static void printTable(List<Row> rows) {
        System.out.printf("%-12s %8s %8s %8s %12s %14s %10s %10s%n",
                "impressions", "clicks", "ctr", "cvr", "gmv", "buyagain_cost",
                "coupon_roi", "weight");
        for (Row row : rows) {
            System.out.printf("%-12d %8d %8.4f %8.4f %12.2f %14.4f %10.4f %10.4f%n",
                    row.get(0).asLong(),
                    row.get(1).asLong(),
                    row.get(2).asDouble(),
                    row.get(3).asDouble(),
                    row.get(4).asDouble(),
                    row.get(5).asDouble(),
                    row.get(6).asDouble(),
                    row.get(7).asDouble());
        }
    }
}
