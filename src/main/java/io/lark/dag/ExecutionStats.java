// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Aggregate view of one graph execution (timing + status per node). */
public final class ExecutionStats {

    private final long totalElapsedNanos;
    private final List<NodeRunStats> nodes;

    ExecutionStats(long totalElapsedNanos, List<NodeRunStats> nodes) {
        this.totalElapsedNanos = totalElapsedNanos;
        this.nodes = List.copyOf(nodes);
    }

    public static ExecutionStats empty() {
        return new ExecutionStats(0, List.of());
    }

    public long totalElapsedNanos() {
        return totalElapsedNanos;
    }

    public List<NodeRunStats> nodes() {
        return nodes;
    }

    /** Human-readable waterfall summary ordered by start time. */
    public String summary() {
        StringBuilder sb = new StringBuilder()
                .append("--- dag run stats (nodes=").append(nodes.size())
                .append(", total=").append(formatNanos(totalElapsedNanos))
                .append(") ---\n");

        nodes.stream()
                .sorted(Comparator.comparingLong(NodeRunStats::startedAtNanos))
                .forEach(node -> sb.append("  [")
                        .append(node.status().displayName()).append("] ")
                        .append(node.id())
                        .append(" (start=").append(formatNanos(node.startedAtNanos()))
                        .append(", elapsed=").append(formatNanos(node.elapsedNanos()))
                        .append(")\n"));
        return sb.toString();
    }

    private static String formatNanos(long nanos) {
        double micros = nanos / 1_000.0;
        if (micros >= 1_000.0) {
            return String.format(Locale.ROOT, "%.2fms", micros / 1_000.0);
        }
        return String.format(Locale.ROOT, "%.2fus", micros);
    }
}
