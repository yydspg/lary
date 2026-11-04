// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo;

import java.util.Arrays;
import java.util.Locale;

/**
 * Warmed-up steady-state benchmark for the complete DAG + column + ranking
 * demo.
 *
 * <p>It measures the per-request path including module build/compile, because
 * a fresh {@code ColumnPipeline} is created for every request. The first
 * iterations are discarded so JIT compilation and class loading do not distort
 * the sample.
 *
 * <p>Run with {@code ./gradlew :examples:runFullBenchmark}. Override the size
 * and sample counts with {@code -Plary.full.candidates}, {@code
 * -Plary.full.warmup} and {@code -Plary.full.iterations}.
 */
public final class FullRankingBenchmark {

    private static final int DEFAULT_CANDIDATES = 1_000;
    private static final int DEFAULT_WARMUP = 8;
    private static final int DEFAULT_ITERATIONS = 20;

    private FullRankingBenchmark() {
    }

    public static void main(String[] args) {
        int candidates = integer("lary.full.candidates", DEFAULT_CANDIDATES);
        int warmup = integer("lary.full.warmup", DEFAULT_WARMUP);
        int iterations = integer("lary.full.iterations", DEFAULT_ITERATIONS);

        System.out.printf(Locale.ROOT,
                "=== full ranking benchmark (candidates=%d, warmup=%d, "
                        + "iterations=%d) ===%n",
                candidates, warmup, iterations);

        for (int i = 0; i < warmup; i++) {
            FullRankingDemo.run(candidates);
        }

        long[] total = new long[iterations];
        long[] dag = new long[iterations];
        long[] column = new long[iterations];
        long[] rank = new long[iterations];
        int selected = 0;
        int filled = 0;

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            FullRankingDemo.Result result = FullRankingDemo.run(candidates);
            total[i] = System.nanoTime() - start;
            dag[i] = result.prepared().dagStats().totalElapsedNanos();
            column[i] = result.column().stats().totalElapsedNanos();
            rank[i] = result.greedy().stats().elapsedNanos()
                    + result.beam().stats().elapsedNanos();
            selected = result.column().selectedCount();
            filled = result.beam().stats().slotsFilled();
        }

        System.out.printf(Locale.ROOT,
                "selected=%d, beam slots filled=%d%n", selected, filled);
        report("total", total);
        report("dag", dag);
        report("column", column);
        report("rank", rank);
        System.out.printf(Locale.ROOT,
                "throughput: %.1f requests/s (total median)%n",
                1_000_000_000.0 / median(total));
    }

    private static void report(String stage, long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        System.out.printf(Locale.ROOT,
                "%-7s median=%s  p95=%s  min=%s  max=%s%n",
                stage,
                millis(median(sorted)),
                millis(percentile(sorted, 0.95)),
                millis(sorted[0]),
                millis(sorted[sorted.length - 1]));
    }

    private static long median(long[] sorted) {
        return percentile(sorted, 0.50);
    }

    private static long percentile(long[] sorted, double percentile) {
        int index = (int) Math.ceil(percentile * sorted.length) - 1;
        return sorted[Math.max(0, Math.min(index, sorted.length - 1))];
    }

    private static String millis(long nanos) {
        return String.format(Locale.ROOT, "%.3fms", nanos / 1_000_000.0);
    }

    private static int integer(String property, int fallback) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
