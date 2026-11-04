// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.model;

import io.lark.dag.ExecutionStats;
import io.lark.examples.search.model.SearchRequest;

/** Validated request and config produced by the DAG stage. */
public record Prepared(SearchRequest request, RankConfig config,
                       ExecutionStats dagStats) {

    public Prepared withStats(ExecutionStats stats) {
        return new Prepared(request, config, stats);
    }
}
