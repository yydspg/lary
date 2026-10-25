// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import io.lark.column.TensorStore;
import io.lark.column.TensorTable;
import io.lark.metric.Monitor;

import java.util.Objects;

/**
 * Per-run context shared by compute nodes: feed / compute / fetch phases, the
 * intermediate store, the feed and fetch tables, and monitoring.
 */
public final class ExecutionContext {

    public enum RunPhase {
        FEED(0, "feed"),
        COMPUTE(1, "compute"),
        FETCH(2, "fetch");

        private final int index;
        private final String label;

        RunPhase(int index, String label) {
            this.index = index;
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    private RunPhase phase = RunPhase.FEED;
    private final long[] phaseElapsedNanos = new long[RunPhase.values().length];
    private long phaseStartNanos;
    private boolean phaseActive;

    private TensorTable feed = new TensorTable();
    private TensorTable fetch = new TensorTable();
    private final TensorStore store = new TensorStore();
    private volatile Monitor monitor;

    public RunPhase phase() {
        return phase;
    }

    public String phaseName() {
        return phase.label();
    }

    public void beginPhase(RunPhase next) {
        endPhase();
        phase = next;
        phaseStartNanos = System.nanoTime();
        phaseActive = true;
    }

    public void endPhase() {
        if (!phaseActive) {
            return;
        }
        phaseElapsedNanos[phase.index] += System.nanoTime() - phaseStartNanos;
        phaseActive = false;
    }

    public long phaseElapsedNanos(RunPhase phase) {
        return phaseElapsedNanos[phase.index];
    }

    public void setFeed(TensorTable table) {
        feed = Objects.requireNonNull(table, "table");
    }

    public TensorTable feed() {
        return feed;
    }

    public void setFetch(TensorTable table) {
        fetch = Objects.requireNonNull(table, "table");
    }

    public TensorTable fetch() {
        return fetch;
    }

    public TensorStore store() {
        return store;
    }

    public void clearStore() {
        store.clear();
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public Monitor monitor() {
        return monitor;
    }

    public boolean hasMonitor() {
        return monitor != null;
    }
}
