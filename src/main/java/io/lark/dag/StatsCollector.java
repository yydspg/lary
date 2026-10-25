// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in per-node monitoring/timing implementation.
 *
 * <p>Attach it to an {@link Executor} to get per-node elapsed times:
 *
 * <pre>{@code
 * StatsCollector stats = new StatsCollector();
 * executor.setMonitor(stats);
 * executor.execute(graph, ctx);
 * System.out.println(stats.stats().summary());
 * }</pre>
 *
 * <p>Thread-safe: callbacks may fire from many node threads.
 */
public final class StatsCollector implements Monitor {

    private final Object lock = new Object();
    private final List<NodeRunStats> records = new ArrayList<>();
    private long totalElapsedNanos;

    @Override
    public void emit(Event event) {
        if (!"dag".equals(event.source())) {
            return;
        }
        if (event.action().startsWith("node.")) {
            handleNodeEvent(event);
        }
    }

    private void handleNodeEvent(Event event) {
        String status = event.get("status").orElse(null);
        if (status == null || "running".equals(status)) {
            return;
        }

        NodeStatus nodeStatus = switch (status) {
            case "failed" -> NodeStatus.FAILED;
            case "fallback" -> NodeStatus.FALLBACK;
            case "skipped" -> NodeStatus.SKIPPED;
            default -> NodeStatus.SUCCESS;
        };

        long elapsed = event.get("elapsed_ns")
                .map(Long::parseLong).orElse(0L);
        long startedAt = event.get("started_ns")
                .map(Long::parseLong).orElse(0L);
        String type = event.get("type").orElse("");

        NodeRunStats record = new NodeRunStats(event.subject(), type,
                nodeStatus, elapsed, startedAt);

        synchronized (lock) {
            records.add(record);
            totalElapsedNanos = Math.max(totalElapsedNanos, startedAt + elapsed);
        }
    }

    /** Immutable snapshot of the current statistics. */
    public ExecutionStats stats() {
        synchronized (lock) {
            return new ExecutionStats(totalElapsedNanos, records);
        }
    }

    public void reset() {
        synchronized (lock) {
            records.clear();
            totalElapsedNanos = 0;
        }
    }
}
