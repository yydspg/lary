// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.column;

import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.flow.FetchStream;
import io.lark.column.context.ColumnRunContext;
import io.lark.column.monitor.ColumnStatsCollector;
import io.lark.column.monitor.RunStats;
import io.lark.column.tensor.Tensor;
import io.lark.examples.demo.model.ColumnStage;
import io.lark.examples.demo.model.RankConfig;
import io.lark.examples.demo.model.ScoredGoods;
import io.lark.examples.search.SearchDemo;
import io.lark.examples.search.model.GoodsDO;
import io.lark.examples.search.model.SearchRequest;
import io.lark.examples.search.module.SearchModule;
import io.lark.examples.search.schema.SearchSchema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Column stage: compute metrics, apply the score and select top-k per PV. */
public final class ColumnScoring {

    private static final int WORKERS = 4;

    private ColumnScoring() {
    }

    public static ColumnStage score(SearchRequest request, RankConfig config) {
        int topK = Math.min(config.topK(), request.goods().size());
        SearchModule module = new SearchModule(topK, WORKERS);
        ColumnStatsCollector stats = new ColumnStatsCollector();
        Tensor adjusted;
        Tensor selected;
        Tensor retarget;
        Tensor delay;
        RunStats runStats;

        try (ColumnPipeline pipeline = new ColumnPipeline(WORKERS)) {
            pipeline.setMonitor(stats);
            pipeline.add(module);

            FetchStream required = FetchStream.of(
                    module.shared(SearchSchema.ADJUSTED_SCORE.doubleColumn()),
                    module.shared(SearchSchema.SELECTED.longColumn()),
                    module.shared(SearchSchema.RETARGET_AD.doubleColumn()),
                    module.shared(SearchSchema.DELAY.doubleColumn()));

            pipeline.setExperiment(request.context().experiment())
                    .executeTensors(module.feedTensorMap(SearchDemo.rows(request)),
                            required,
                            ColumnRunContext.of(request.context()));
            adjusted = pipeline.fetchTensor(
                    SearchSchema.ADJUSTED_SCORE.columnName());
            selected = pipeline.fetchTensor(
                    SearchSchema.SELECTED.columnName());
            retarget = pipeline.fetchTensor(
                    SearchSchema.RETARGET_AD.columnName());
            delay = pipeline.fetchTensor(SearchSchema.DELAY.columnName());
            runStats = pipeline.stats();
        }

        List<GoodsDO> goods = request.goods().goods();
        List<ScoredGoods> candidates = new ArrayList<>(topK);
        int selectedCount = 0;
        for (int i = 0; i < goods.size(); i++) {
            if (selected.getLong(i) != 1L) {
                continue;
            }
            selectedCount++;
            GoodsDO item = goods.get(i);
            candidates.add(new ScoredGoods(
                    item,
                    adjusted.getDouble(i),
                    (int) (item.goodsId() % 8),
                    (int) (item.goodsId() % 10),
                    item.couponCostFen(),
                    item.goodsId() % 9 == 0));
        }
        candidates.sort(Comparator
                .comparingDouble(ScoredGoods::baseScore).reversed());
        return new ColumnStage(candidates, selectedCount, retarget, delay,
                runStats);
    }
}
