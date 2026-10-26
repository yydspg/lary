// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

/** One selected goods item in final ranking order. */
public record RankedGoods(int rank, long goodsId, double adjustedScore) {

    public RankedGoods {
        if (rank <= 0) {
            throw new IllegalArgumentException("rank must be positive");
        }
        if (goodsId < 0) {
            throw new IllegalArgumentException("goodsId must be non-negative");
        }
    }
}
