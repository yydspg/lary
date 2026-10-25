// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Detects repeated timeouts per logical op (node type / op name).
 *
 * <p>Thread-safe and intended to live across graph runs, so an op that keeps
 * timing out can be quarantined instead of blocking the system again and
 * again.
 */
public final class TimeoutTracker {

    /** Immutable snapshot for one key. */
    public record Entry(
            String key,
            int consecutiveTimeouts,
            long totalTimeouts,
            boolean quarantined,
            long quarantineUntilNanos,
            long lastTimeoutNanos
    ) {
    }

    private static final class Counter {
        private int consecutiveTimeouts;
        private long totalTimeouts;
        private long quarantineUntilNanos;
        private long lastTimeoutNanos;
    }

    private final ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<>();

    /**
     * Record one timeout and apply the policy's quarantine threshold.
     * Returns the updated snapshot.
     */
    public Entry recordTimeout(String key, TimeoutPolicy policy) {
        Counter counter = counters.computeIfAbsent(key, ignored -> new Counter());
        synchronized (counter) {
            counter.consecutiveTimeouts++;
            counter.totalTimeouts++;
            counter.lastTimeoutNanos = System.nanoTime();

            if (policy.quarantineEnabled()
                    && counter.consecutiveTimeouts >= policy.maxConsecutiveTimeouts()) {
                counter.quarantineUntilNanos = policy.quarantineDuration().isZero()
                        ? Long.MAX_VALUE
                        : saturatingAdd(System.nanoTime(),
                                policy.quarantineDuration().toNanos());
            }
            return snapshot(key, counter);
        }
    }

    /** Reset the consecutive count after a successful execution. */
    public void recordSuccess(String key) {
        Counter counter = counters.get(key);
        if (counter == null) {
            return;
        }
        synchronized (counter) {
            counter.consecutiveTimeouts = 0;
            counter.quarantineUntilNanos = 0;
        }
    }

    /** Whether the key is currently quarantined (expired entries are cleared). */
    public boolean isQuarantined(String key) {
        Counter counter = counters.get(key);
        if (counter == null) {
            return false;
        }
        synchronized (counter) {
            if (counter.quarantineUntilNanos == 0) {
                return false;
            }
            if (counter.quarantineUntilNanos == Long.MAX_VALUE
                    || System.nanoTime() < counter.quarantineUntilNanos) {
                return true;
            }
            counter.quarantineUntilNanos = 0;
            counter.consecutiveTimeouts = 0;
            return false;
        }
    }

    public Entry snapshot(String key) {
        Counter counter = counters.get(key);
        if (counter == null) {
            return new Entry(key, 0, 0, false, 0, 0);
        }
        synchronized (counter) {
            return snapshot(key, counter);
        }
    }

    public List<Entry> snapshot() {
        List<Entry> entries = new ArrayList<>(counters.size());
        for (String key : counters.keySet()) {
            entries.add(snapshot(key));
        }
        entries.sort(Comparator.comparing(Entry::key));
        return entries;
    }

    public void reset(String key) {
        counters.remove(key);
    }

    public void resetAll() {
        counters.clear();
    }

    private static Entry snapshot(String key, Counter counter) {
        boolean quarantined = counter.quarantineUntilNanos == Long.MAX_VALUE
                || (counter.quarantineUntilNanos != 0
                    && System.nanoTime() < counter.quarantineUntilNanos);
        return new Entry(key, counter.consecutiveTimeouts,
                counter.totalTimeouts, quarantined,
                counter.quarantineUntilNanos, counter.lastTimeoutNanos);
    }

    private static long saturatingAdd(long left, long right) {
        long result = left + right;
        return result < 0 ? Long.MAX_VALUE : result;
    }

    /** Convenience for monitoring display. */
    public String summary() {
        StringBuilder sb = new StringBuilder("--- timeout tracker ---\n");
        for (Entry entry : snapshot()) {
            sb.append("  ").append(entry.key())
                    .append(": consecutive=").append(entry.consecutiveTimeouts())
                    .append(", total=").append(entry.totalTimeouts())
                    .append(", quarantined=").append(entry.quarantined())
                    .append('\n');
        }
        return sb.toString();
    }

    /** Duration helper used by callers that keep absolute nanoTime values. */
    public static Duration remainingQuarantine(Entry entry) {
        if (!entry.quarantined() || entry.quarantineUntilNanos() == Long.MAX_VALUE) {
            return Duration.ZERO;
        }
        long remaining = entry.quarantineUntilNanos() - System.nanoTime();
        return remaining <= 0 ? Duration.ZERO : Duration.ofNanos(remaining);
    }
}
