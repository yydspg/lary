// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import io.lark.metric.Event;
import io.lark.metric.Monitor;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

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

    private final Queue<NodeRunStats> records = new ConcurrentLinkedQueue<>();
    private final AtomicLong totalElapsedNanos = new AtomicLong();

    @Override
    public void emit(Event event) {
        if (!"dag".equals(event.source())) {
            return;
        }
        if (event.action().startsWith("node.")
                || event.action().startsWith("op.")) {
            handleNodeEvent(event);
        }
    }

    private void handleNodeEvent(Event event) {
        String status = event.get("status").orElse(null);
        if (status == null || "running".equals(status)
                || "quarantined".equals(status)) {
            return;
        }

        NodeStatus nodeStatus = switch (status) {
            case "failed" -> NodeStatus.FAILED;
            case "timed_out" -> NodeStatus.TIMED_OUT;
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

        records.add(record);
        long end = startedAt + elapsed;
        totalElapsedNanos.accumulateAndGet(end, Math::max);
    }

    /** Immutable snapshot of the current statistics. */
    public ExecutionStats stats() {
        return new ExecutionStats(totalElapsedNanos.get(), List.copyOf(records));
    }

    public void reset() {
        records.clear();
        totalElapsedNanos.set(0);
    }
}
