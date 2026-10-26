// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.data.Row;

import java.util.List;

/** Prints the scalar search metrics. */
public final class SearchResultPrinter {

    private SearchResultPrinter() {
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
}
