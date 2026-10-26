// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnOps;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.schema.SearchMetrics;

/** Applies perturbation and original score: {@code score * p + original}. */
@ColumnComponent
public final class AdjustedScoreModule extends ColumnModule {

    private final RankingScoreModule ranking;
    private ColumnStream<Double> adjusted;

    public AdjustedScoreModule(RankingScoreModule ranking) {
        this.ranking = ranking;
    }

    @Override
    protected void define() {
        adjusted = node(ColumnOps.mulAdd(SearchMetrics.ADJUSTED_SCORE),
                ranking.published(SearchMetrics.RANKING_SCORE),
                feed(SearchMetrics.PERTURBATION),
                feed(SearchMetrics.ORIGINAL_SCORE));
        dependsOn(RankingScoreModule.class);
        output(adjusted);
    }

    public ColumnStream<Double> adjustedScore() {
        return adjusted;
    }
}
