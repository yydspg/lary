// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnOps;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.SearchRankingOptions;
import io.lark.examples.search.schema.SearchMetrics;

/** PV-level integration: choose top-k goods for each PV. */
@ColumnComponent
public final class TopKModule extends ColumnModule {

    private final AdjustedScoreModule scores;
    private final SearchRankingOptions options;
    private ColumnStream<Long> selected;

    public TopKModule(AdjustedScoreModule scores, SearchRankingOptions options) {
        this.scores = scores;
        this.options = options;
    }

    @Override
    protected void define() {
        selected = node(ColumnOps.topK(SearchMetrics.SELECTED, options.topK()),
                scores.published(SearchMetrics.ADJUSTED_SCORE),
                feed(SearchMetrics.PV_ID));
        dependsOn(AdjustedScoreModule.class);
        output(selected);
    }

    public ColumnStream<Long> selected() {
        return selected;
    }
}
