// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import io.lark.column.context.ExperimentContext;

import java.util.Objects;

/** One search page-view request, including its experiment assignment. */
public record SearchContext(long pvId, long userId, String query, int topK,
                            ExperimentContext experiment) {

    public SearchContext(long pvId, long userId, String query, int topK) {
        this(pvId, userId, query, topK, ExperimentContext.empty());
    }

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
        experiment = Objects.requireNonNull(experiment, "experiment");
    }
}
