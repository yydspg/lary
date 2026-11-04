// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

/**
 * Execution counters for one ranking run.
 *
 * @param candidates input candidate count
 * @param slotsRequested requested sequence length
 * @param slotsFilled actually filled slots
 * @param admittedTotal sum of admitted candidates over all slots
 * @param scoreCalls number of scorer invocations
 * @param sortCalls number of radix-sort invocations
 * @param elapsedNanos wall time for the whole run
 * @param timedOut whether a latency budget stopped the run early
 */
public record RankingStats(int candidates, int slotsRequested, int slotsFilled,
                           int admittedTotal, int scoreCalls, int sortCalls,
                           long elapsedNanos, boolean timedOut,
                           String strategy) {
}
