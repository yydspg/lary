// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.model;

import io.lark.column.context.ExperimentContext;

/** One search request identity, size and experiment assignment. */
public record SearchContext(long pvId, long userId, String query, int topK,
                            ExperimentContext experiment) {

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
        experiment = experiment == null
                ? ExperimentContext.empty() : experiment;
    }

    public SearchContext(long pvId, long userId, String query, int topK) {
        this(pvId, userId, query, topK, ExperimentContext.empty());
    }
}
