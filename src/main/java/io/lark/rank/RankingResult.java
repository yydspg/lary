// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.List;

/** Filled slots plus execution counters. */
public record RankingResult<T>(List<RankedSlot<T>> slots, RankingStats stats) {

    public RankingResult {
        slots = List.copyOf(slots);
    }
}
