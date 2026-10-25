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

    public long count(NodeStatus status) {
        return nodes.stream().filter(node -> node.status() == status).count();
    }

    public long successCount() {
        return count(NodeStatus.SUCCESS);
    }

    public long failureCount() {
        return count(NodeStatus.FAILED) + count(NodeStatus.TIMED_OUT);
    }

    public long fallbackCount() {
        return count(NodeStatus.FALLBACK);
    }

    public long skippedCount() {
        return count(NodeStatus.SKIPPED);
    }

    /** Human-readable waterfall summary ordered by start time. */
    public String summary() {
        StringBuilder sb = new StringBuilder()
                .append("--- dag run stats (nodes=").append(nodes.size())
                .append(", total=").append(formatNanos(totalElapsedNanos))
                .append(", success=").append(successCount())
                .append(", fallback=").append(fallbackCount())
                .append(", failed=").append(failureCount())
                .append(", skipped=").append(skippedCount())
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
