// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.monitor.RunStats;
import io.lark.column.tensor.Tensor;
import io.lark.examples.search.data.SearchContext;

import java.util.List;
import java.util.Objects;

/** Business-level result of one search request. */
public record SearchResult(
        SearchContext context,
        int candidateCount,
        List<RankedGoods> ranked,
        Tensor retargetAd,
        Tensor delay,
        RunStats stats) {

    public SearchResult {
        Objects.requireNonNull(context, "context");
        if (candidateCount < 0) {
            throw new IllegalArgumentException("candidateCount must be non-negative");
        }
        ranked = List.copyOf(Objects.requireNonNull(ranked, "ranked"));
        Objects.requireNonNull(retargetAd, "retargetAd");
        Objects.requireNonNull(delay, "delay");
        Objects.requireNonNull(stats, "stats");
    }
}
