// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.List;

/**
 * Re-scores the admitted candidates for one slot.
 *
 * <p>This is the bridge to whatever computation the business needs. A column
 * subgraph can be executed here over {@code candidates}; the framework does not
 * know or care about tensors, columns or pipelines. The returned array must
 * align with {@code candidates}; non-finite scores are ignored.
 */
@FunctionalInterface
public interface SlotScorer<T, C> {

    double[] score(RankingContext<T, C> context, List<T> candidates);
}
