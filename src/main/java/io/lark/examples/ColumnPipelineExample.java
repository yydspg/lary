// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.column.Cell;
import io.lark.column.Row;
import io.lark.column.Simd;
import io.lark.column.biz.Module;
import io.lark.column.biz.Pipeline;
import io.lark.column.monitor.RunStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Column-engine quick start: feed → compute → fetch, with the SIMD kernels
 * running on {@code jdk.incubator.vector}.
 *
 * <p>Run with {@code ./gradlew :examples:runColumn}.
 */
public final class ColumnPipelineExample {

    private ColumnPipelineExample() {
    }

    public static void main(String[] args) {
        System.out.println("SIMD: " + Simd.summary());

        Module feature = new Module("feature");
        feature.input("x").input("y")
                .output("score").output("filtered").output("total")
                .fromDsl(
                        "score = x * 2 + y\n"
                                + "mask = x > 3\n"
                                + "filtered = filter(score, mask)\n"
                                + "total = sum(score)\n");

        try (Pipeline pipeline = new Pipeline()) {
            pipeline.addModule(feature);
            pipeline.compile();

            List<Row> rows = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                rows.add(Row.of(Cell.of(i), Cell.of(i * 10)));
            }

            pipeline.feed(List.of("x", "y"), rows);
            pipeline.compute();

            List<Row> scores = pipeline.fetch(List.of("score"));
            List<Row> filtered = pipeline.fetch(List.of("filtered"));
            double total = pipeline.fetchScalar("total");

            System.out.println("scores   = " + scores.stream()
                    .map(row -> row.get(0).asLong()).toList());
            System.out.println("filtered = " + filtered.stream()
                    .map(row -> row.get(0).asLong()).toList());
            System.out.println("total    = " + total);

            RunStats stats = pipeline.stats();
            System.out.print(stats.summary());
        }
    }
}
