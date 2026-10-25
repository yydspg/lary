// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.monitor;

import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Aggregate timing, shape and status records for one column pipeline run. */
public final class RunStats {

    public record OpTiming(String nodeId, String opType, String module,
                           long elapsedNanos, String status) {
        /** Compatibility constructor for callers that do not track status. */
        public OpTiming(String nodeId, String opType, String module,
                        long elapsedNanos) {
            this(nodeId, opType, module, elapsedNanos, "unknown");
        }
    }

    public record ModuleTiming(String module, long elapsedNanos, int nodeCount) {
    }

    public record CpuPressure(String pool, double utilization, long busyNanos,
                              long wallNanos, int workers) {
    }

    long feedElapsedNanos;
    long computeElapsedNanos;
    long fetchElapsedNanos;
    int computeWorkers;
    boolean virtualComputeThreads;
    int feedRows;
    int feedColumns;
    int fetchRows;
    int fetchColumns;
    int computedNodes;
    // Node completion is concurrent; queue snapshots keep the emitter off the
    // stats lock while preserving the public List view.
    final Queue<OpTiming> ops = new ConcurrentLinkedQueue<>();
    final Queue<ModuleTiming> modules = new ConcurrentLinkedQueue<>();
    final Queue<CpuPressure> pressure = new ConcurrentLinkedQueue<>();

    public long feedElapsedNanos() {
        return feedElapsedNanos;
    }

    public long computeElapsedNanos() {
        return computeElapsedNanos;
    }

    public long fetchElapsedNanos() {
        return fetchElapsedNanos;
    }

    public int computeWorkers() {
        return computeWorkers;
    }

    public boolean virtualComputeThreads() {
        return virtualComputeThreads;
    }

    public int feedRows() {
        return feedRows;
    }

    public int feedColumns() {
        return feedColumns;
    }

    public int fetchRows() {
        return fetchRows;
    }

    public int fetchColumns() {
        return fetchColumns;
    }

    public int computedNodes() {
        return computedNodes;
    }

    public long totalElapsedNanos() {
        return feedElapsedNanos + computeElapsedNanos + fetchElapsedNanos;
    }

    public long successCount() {
        return countStatus("success");
    }

    public long fallbackCount() {
        return countStatus("fallback");
    }

    public long failureCount() {
        return countStatus("failed");
    }

    public List<OpTiming> ops() {
        return List.copyOf(ops);
    }

    public List<ModuleTiming> modules() {
        return List.copyOf(modules);
    }

    public List<CpuPressure> pressure() {
        return List.copyOf(pressure);
    }

    public void reset() {
        feedElapsedNanos = 0;
        computeElapsedNanos = 0;
        fetchElapsedNanos = 0;
        computeWorkers = 0;
        virtualComputeThreads = false;
        feedRows = 0;
        feedColumns = 0;
        fetchRows = 0;
        fetchColumns = 0;
        computedNodes = 0;
        ops.clear();
        modules.clear();
        pressure.clear();
    }

    /** Human-readable report. */
    public String summary() {
        StringBuilder sb = new StringBuilder("--- run stats ---\n");
        sb.append("  feed   : ").append(formatNanos(feedElapsedNanos))
                .append(" (").append(feedRows).append(" rows x ")
                .append(feedColumns).append(" cols)\n");
        sb.append("  compute: ").append(formatNanos(computeElapsedNanos))
                .append(" (workers=").append(computeWorkers)
                .append(virtualComputeThreads ? ", virtual" : ", platform")
                .append(", nodes=").append(computedNodes).append(")\n");
        sb.append("  fetch  : ").append(formatNanos(fetchElapsedNanos))
                .append(" (").append(fetchRows).append(" rows x ")
                .append(fetchColumns).append(" cols)\n");
        sb.append("  total  : ").append(formatNanos(totalElapsedNanos()))
                .append('\n');
        if (!ops.isEmpty()) {
            sb.append("  ops    : success=").append(successCount())
                    .append(", fallback=").append(fallbackCount())
                    .append(", failed=").append(failureCount()).append('\n');
        }

        if (!modules.isEmpty()) {
            List<ModuleTiming> sorted = new ArrayList<>(modules);
            sorted.sort(Comparator.comparingLong(ModuleTiming::elapsedNanos).reversed());
            sb.append("  modules (").append(modules.size()).append("):\n");
            for (ModuleTiming module : sorted) {
                sb.append("    ").append(module.module()).append(": ")
                        .append(formatNanos(module.elapsedNanos()))
                        .append(" (").append(module.nodeCount()).append(" nodes)\n");
            }
        }

        if (!pressure.isEmpty()) {
            sb.append("  cpu pressure:\n");
            for (CpuPressure p : pressure) {
                sb.append("    ").append(p.pool())
                        .append(": util=").append(String.format(Locale.ROOT, "%.1f%%",
                                p.utilization() * 100.0))
                        .append(" busy=").append(formatNanos(p.busyNanos()))
                        .append(" wall=").append(formatNanos(p.wallNanos()))
                        .append(" workers=").append(p.workers()).append('\n');
            }
        }

        if (!ops.isEmpty()) {
            List<OpTiming> sorted = new ArrayList<>(ops);
            sorted.sort(Comparator.comparingLong(OpTiming::elapsedNanos).reversed());
            sb.append("  slowest ops:\n");
            int limit = Math.min(sorted.size(), 8);
            for (int i = 0; i < limit; i++) {
                OpTiming op = sorted.get(i);
                sb.append("    [").append(op.module()).append("] ")
                        .append(op.nodeId()).append(" (").append(op.opType())
                        .append('/').append(op.status()).append("): ")
                        .append(formatNanos(op.elapsedNanos()))
                        .append('\n');
            }
        }

        return sb.toString();
    }

    private long countStatus(String status) {
        long count = 0;
        for (OpTiming op : ops) {
            if (status.equals(op.status())) {
                count++;
            }
        }
        return count;
    }

    static String formatNanos(long nanos) {
        double micros = nanos / 1000.0;
        if (micros >= 1000.0) {
            return String.format(Locale.ROOT, "%.2fms", micros / 1000.0);
        }
        return String.format(Locale.ROOT, "%.2fus", micros);
    }
}
