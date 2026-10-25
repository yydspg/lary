// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

import java.util.Map;
import java.util.TreeMap;

/**
 * Aggregate counters and durations per action plus a total error count.
 * Mirrors {@code lark::metric::StatsMonitor}.
 */
public final class StatsMonitor implements Monitor {

    private long events;
    private long errors;
    private final Map<String, Long> countByAction = new TreeMap<>();
    private final Map<String, Long> durationByAction = new TreeMap<>();

    @Override
    public synchronized void emit(Event event) {
        events++;
        if (!event.ok()) {
            errors++;
        }
        countByAction.merge(event.action(), 1L, Long::sum);
        durationByAction.merge(event.action(), event.durationNanos(), Long::sum);
    }

    public synchronized long eventCount() {
        return events;
    }

    public synchronized long errorCount() {
        return errors;
    }

    public synchronized long count(String action) {
        return countByAction.getOrDefault(action, 0L);
    }

    public synchronized long durationNanos(String action) {
        return durationByAction.getOrDefault(action, 0L);
    }

    public synchronized String summary() {
        StringBuilder sb = new StringBuilder()
                .append("--- metric stats (events=").append(events)
                .append(", errors=").append(errors).append(") ---\n");
        for (Map.Entry<String, Long> entry : countByAction.entrySet()) {
            long duration = durationByAction.getOrDefault(entry.getKey(), 0L);
            sb.append("  ").append(entry.getKey())
                    .append(": count=").append(entry.getValue())
                    .append(", duration=").append(duration).append("ns\n");
        }
        return sb.toString();
    }
}
