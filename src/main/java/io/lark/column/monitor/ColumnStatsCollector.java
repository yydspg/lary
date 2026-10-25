// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.monitor;

import io.lark.metric.Event;
import io.lark.metric.Monitor;

/**
 * Column-engine monitoring implementation: turns "column.*" events into the
 * domain {@link RunStats} model. Thread-safe.
 */
public final class ColumnStatsCollector implements Monitor {

    private final Object lock = new Object();
    private final RunStats stats = new RunStats();

    @Override
    public void emit(Event event) {
        if (!"column".equals(event.source())) {
            return;
        }
        switch (event.action()) {
            case "feed.end" -> {
                synchronized (lock) {
                    stats.feedElapsedNanos = event.durationNanos();
                    stats.feedRows = intAttr(event, "rows");
                    stats.feedColumns = intAttr(event, "columns");
                }
            }
            case "compute.start", "compute.end" -> {
                synchronized (lock) {
                    if ("compute.end".equals(event.action())) {
                        stats.computeElapsedNanos = event.durationNanos();
                    }
                    stats.computedNodes = intAttr(event, "nodes");
                    stats.computeWorkers = intAttr(event, "workers");
                    stats.virtualComputeThreads =
                            Boolean.parseBoolean(event.get("virtual").orElse("false"));
                }
            }
            case "fetch.end" -> {
                synchronized (lock) {
                    stats.fetchElapsedNanos = event.durationNanos();
                    stats.fetchRows = intAttr(event, "rows");
                    stats.fetchColumns = intAttr(event, "columns");
                }
            }
            case "node.end" -> handleNode(event, "success");
            case "node.error" -> handleNode(event, "failed");
            case "node.fallback" -> handleNode(event, "fallback");
            case "module.end" -> handleModule(event);
            case "cpu.pressure" -> handlePressure(event);
            default -> {
                // start events are informational
            }
        }
    }

    private void handleNode(Event event, String status) {
        String op = event.get("op").orElse("");
        String module = event.get("module").orElseGet(() -> moduleOf(event.subject()));
        stats.ops.add(new RunStats.OpTiming(
                event.subject(), op, module, event.durationNanos(), status));
    }

    private static int intAttr(Event event, String name) {
        return event.get(name).map(Integer::parseInt).orElse(0);
    }

    private void handleModule(Event event) {
        int count = event.get("node_count").map(Integer::parseInt).orElse(0);
        stats.modules.add(new RunStats.ModuleTiming(
                event.subject(), event.durationNanos(), count));
    }

    private void handlePressure(Event event) {
        String pool = event.get("pool").orElse("compute");
        long busy = event.get("busy_ns").map(Long::parseLong).orElse(0L);
        long wall = event.get("wall_ns").map(Long::parseLong).orElse(0L);
        int workers = event.get("workers").map(Integer::parseInt).orElse(0);
        double utilization = event.get("utilization")
                .map(Double::parseDouble).orElse(0.0);
        synchronized (lock) {
            stats.computeWorkers = workers;
        }
        stats.pressure.add(new RunStats.CpuPressure(
                pool, utilization, busy, wall, workers));
    }

    public RunStats stats() {
        return stats;
    }

    public void reset() {
        synchronized (lock) {
            stats.reset();
        }
    }

    private static String moduleOf(String nodeId) {
        int slash = nodeId.indexOf('/');
        return slash < 0 ? nodeId : nodeId.substring(0, slash);
    }
}
