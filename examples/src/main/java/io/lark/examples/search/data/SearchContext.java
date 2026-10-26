// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

/** One search page-view request. */
public record SearchContext(long pvId, long userId, String query, int topK) {

    public SearchContext {
        if (pvId < 0) {
            throw new IllegalArgumentException("pvId must be non-negative");
        }
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be blank");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }
    }
}
