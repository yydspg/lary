// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys;

import io.lark.column.data.Row;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.column.biz.FeedStream;
import io.lark.examples.recsys.data.MetricsFeed;
import io.lark.examples.recsys.data.MetricsRepository;
import io.lark.examples.recsys.module.FunnelMetricModule;
import io.lark.examples.recsys.module.PerformanceSummaryModule;
import io.lark.examples.recsys.module.RankingWeightModule;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Steady-state benchmark for the search/recommendation pipeline.
 *
 * <p>The normal demo prints {@code RunStats} for a single tiny run, which is
 * useful for debugging but dominated by JIT and class loading. This benchmark
 * performs unmeasured warm-up iterations, then reports median and p95 timings
 * for feed, compute and fetch over repeated runs.
 *
 * <p>Run with {@code ./gradlew :examples:runRecsysBenchmark}. Override sizes
 * with {@code -Plary.bench.rows=...}, {@code -Plary.bench.warmup=...} and
 * {@code -Plary.bench.iterations=...}.
 */
public final class RecSysBenchmark {

    private RecSysBenchmark() {
    }

    public static void main(String[] args) {
        int rows = intProperty("lary.bench.rows", 100_000);
        int warmup = Math.max(0, intProperty("lary.bench.warmup", 10));
        int iterations = intProperty("lary.bench.iterations", 30);
        int workers = intProperty("lary.bench.workers", 4);
        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be positive");
        }
        if (workers <= 0) {
            throw new IllegalArgumentException("workers must be positive");
        }

        List<Row> input = repeat(MetricsRepository.rows(), rows);
        FunnelMetricModule metrics = new FunnelMetricModule();
        RankingWeightModule ranking = new RankingWeightModule(metrics);
        PerformanceSummaryModule summary =
                new PerformanceSummaryModule(metrics, ranking);
        FeedStream feed = MetricsFeed.of(metrics, input);
        FetchStream fetch = FetchStream.of(ranking.weight());

        long[] feedNanos = new long[iterations];
        long[] computeNanos = new long[iterations];
        long[] fetchNanos = new long[iterations];
        long[] totalNanos = new long[iterations];

        try (ColumnPipeline pipeline = new ColumnPipeline(workers)) {
            pipeline.addAll(metrics, ranking, summary).compile();

            for (int i = 0; i < warmup; i++) {
                runOnce(pipeline, feed, fetch);
            }

            for (int i = 0; i < iterations; i++) {
                pipeline.stats().reset();
                long start = System.nanoTime();
                pipeline.feed(feed);
                long fed = System.nanoTime();
                pipeline.compute();
                long computed = System.nanoTime();
                pipeline.fetch(fetch);
                long fetched = System.nanoTime();

                feedNanos[i] = fed - start;
                computeNanos[i] = computed - fed;
                fetchNanos[i] = fetched - computed;
                totalNanos[i] = fetched - start;
            }

            System.out.println("=== recsys benchmark ===");
            System.out.printf(Locale.ROOT,
                    "rows=%d warmup=%d iterations=%d workers=%d%n",
                    rows, warmup, iterations, workers);
            printRow("feed", feedNanos, rows);
            printRow("compute", computeNanos, rows);
            printRow("fetch", fetchNanos, rows);
            printRow("total", totalNanos, rows);
        }
    }

    private static void runOnce(ColumnPipeline pipeline, FeedStream feed,
                                FetchStream fetch) {
        pipeline.feed(feed);
        pipeline.compute();
        pipeline.fetch(fetch);
    }

    private static void printRow(String phase, long[] samples, int rows) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        long median = sorted[sorted.length / 2];
        long p95 = sorted[Math.min(sorted.length - 1,
                (int) Math.ceil(sorted.length * 0.95) - 1)];
        double rowsPerSecond = rows * 1_000_000_000.0 / Math.max(1, median);
        System.out.printf(Locale.ROOT,
                "  %-8s median=%9s p95=%9s  %,12.0f rows/s%n",
                phase, format(median), format(p95), rowsPerSecond);
    }

    private static String format(long nanos) {
        if (nanos >= 1_000_000) {
            return String.format(Locale.ROOT, "%.2fms", nanos / 1_000_000.0);
        }
        return String.format(Locale.ROOT, "%.2fus", nanos / 1_000.0);
    }

    private static List<Row> repeat(List<Row> base, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("rows must be positive");
        }
        Row[] rows = new Row[count];
        for (int i = 0; i < count; i++) {
            rows[i] = base.get(i % base.size());
        }
        return List.of(rows);
    }

    private static int intProperty(String name, int fallback) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return Integer.parseInt(value);
    }
}
