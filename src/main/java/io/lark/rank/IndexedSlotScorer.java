// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.List;

/**
 * Scorer that works directly on candidate indexes.
 *
 * <p>Use this form when the scorer builds columns/tensors from the original
 * list: it avoids allocating an intermediate {@code List<T>} on every slot.
 * The returned array must align with {@code indices[0..count)}.
 */
@FunctionalInterface
public interface IndexedSlotScorer<T, C> {

    double[] score(RankingContext<T, C> context, int[] indices, int count);
}
