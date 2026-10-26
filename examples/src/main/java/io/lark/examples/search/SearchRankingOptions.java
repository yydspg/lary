// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

/** Search ranking configuration shared by the demo and Spring wiring. */
public record SearchRankingOptions(int topK) {

    public SearchRankingOptions {
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }
    }
}
