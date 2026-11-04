// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

/**
 * One filled slot.
 *
 * @param slot zero-based slot index
 * @param index original candidate index in the input list
 * @param item candidate business object
 * @param score score that selected this candidate for the slot
 */
public record RankedSlot<T>(int slot, int index, T item, double score) {

    public RankedSlot {
        if (slot < 0) {
            throw new IllegalArgumentException("slot must be non-negative");
        }
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
    }
}
