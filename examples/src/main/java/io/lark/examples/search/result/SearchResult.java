// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.result;

import io.lark.column.monitor.RunStats;
import io.lark.examples.search.model.SearchContext;
import io.lark.column.tensor.Tensor;

import java.util.List;

/** Typed result of one search request. */
public record SearchResult(SearchContext context,
                           int candidateCount,
                           List<RankedGoods> ranked,
                           Tensor retargetAd,
                           Tensor delay,
                           RunStats stats) {

    public SearchResult {
        ranked = List.copyOf(ranked);
    }

    /** One selected goods item in final ranking order. */
    public record RankedGoods(int rank, long goodsId, double adjustedScore) {
    }
}
