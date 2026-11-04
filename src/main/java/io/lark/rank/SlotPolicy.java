// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

/**
 * Combined business policy for one sequential ranker.
 *
 * <p>Implement this when admission and scoring share state or rules; otherwise
 * the separate {@link SlotAdmission} and {@link SlotScorer} can be wired
 * independently.
 */
public interface SlotPolicy<T, C> extends SlotAdmission<T, C>, SlotScorer<T, C> {
}
