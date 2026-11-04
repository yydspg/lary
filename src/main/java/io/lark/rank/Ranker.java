// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.List;

/**
 * Common contract for sequential ranking strategies.
 *
 * <p>Greedy, beam search and backtracking implement this interface, so business
 * code can choose a strategy without changing the admission/scorer wiring. A
 * custom exact DP or ILP implementation can plug into the same contract.
 */
public interface Ranker<T, C> {

    List<RankedSlot<T>> rank(List<T> candidates);

    List<RankedSlot<T>> rank(List<T> candidates, C businessContext);

    RankingResult<T> rankWithStats(List<T> candidates, C businessContext);
}
