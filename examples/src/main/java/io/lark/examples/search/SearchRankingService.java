// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.FetchStream;
import io.lark.column.data.Row;
import io.lark.examples.search.data.SearchFeed;
import io.lark.examples.search.data.SearchRequest;
import io.lark.examples.search.module.AdjustedScoreModule;
import io.lark.examples.search.module.BuyAgainModule;
import io.lark.examples.search.module.CouponRoiModule;
import io.lark.examples.search.module.CtCvrModule;
import io.lark.examples.search.module.CtrModule;
import io.lark.examples.search.module.CvrModule;
import io.lark.examples.search.module.DelayModule;
import io.lark.examples.search.module.GmvModule;
import io.lark.examples.search.module.RankingScoreModule;
import io.lark.examples.search.module.RebuySameLossModule;
import io.lark.examples.search.module.RetargetAdModule;
import io.lark.examples.search.module.SearchInputModule;
import io.lark.examples.search.module.TopKModule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Business facade for one search request.
 *
 * <p>Callers pass a {@link SearchRequest} and receive a typed
 * {@link SearchResult}; they do not build modules, compile a graph, or read
 * positional {@link Row} cells. The service creates a fresh pipeline per
 * request, so it can be reused by concurrent callers.
 */
public final class SearchRankingService {

    private static final int GOODS_ID_COLUMN = 0;
    private static final int SCORE_COLUMN = 1;
    private static final int SELECTED_COLUMN = 2;

    private final SearchInputModule inputs;
    private final AdjustedScoreModule scores;
    private final TopKModule topK;
    private final RetargetAdModule retarget;
    private final DelayModule delay;
    private final Supplier<ColumnPipeline> pipelines;

    /**
     * Create a service around an externally supplied pipeline factory. Spring
     * injects a {@code ColumnPipelineFactory::create} adapter here; the demo
     * passes a supplier that creates a plain in-process pipeline.
     */
    public SearchRankingService(SearchInputModule inputs,
                                AdjustedScoreModule scores,
                                TopKModule topK,
                                RetargetAdModule retarget,
                                DelayModule delay,
                                Supplier<ColumnPipeline> pipelines) {
        this.inputs = Objects.requireNonNull(inputs, "inputs");
        this.scores = Objects.requireNonNull(scores, "scores");
        this.topK = Objects.requireNonNull(topK, "topK");
        this.retarget = Objects.requireNonNull(retarget, "retarget");
        this.delay = Objects.requireNonNull(delay, "delay");
        this.pipelines = Objects.requireNonNull(pipelines, "pipelines");
    }

    /**
     * Plain-Java composition root for the demo and CLI tests. Spring users do
     * not call this: component scanning discovers and injects the modules.
     */
    public static SearchRankingService create(int topKValue, int workers) {
        SearchInputModule inputs = new SearchInputModule();
        CtrModule ctr = new CtrModule();
        CvrModule cvr = new CvrModule();
        CtCvrModule ctcvr = new CtCvrModule(ctr, cvr);
        GmvModule gmv = new GmvModule();
        CouponRoiModule coupon = new CouponRoiModule(gmv);
        BuyAgainModule buyAgain = new BuyAgainModule();
        RebuySameLossModule rebuy = new RebuySameLossModule(buyAgain);
        RankingScoreModule ranking = new RankingScoreModule(
                ctr, cvr, gmv, coupon, rebuy);
        AdjustedScoreModule adjusted = new AdjustedScoreModule(ranking);
        TopKModule topK = new TopKModule(adjusted,
                new SearchRankingOptions(topKValue));
        RetargetAdModule retarget = new RetargetAdModule();
        DelayModule delay = new DelayModule();

        List<ColumnModule> modules = List.of(
                inputs, ctr, cvr, ctcvr, gmv, coupon, buyAgain, rebuy,
                ranking, adjusted, topK, retarget, delay);
        return new SearchRankingService(inputs, adjusted, topK, retarget, delay,
                () -> {
                    ColumnPipeline pipeline = new ColumnPipeline(workers);
                    pipeline.addAll(modules);
                    return pipeline;
                });
    }

    /** Execute one request and return typed ranking and shaped signals. */
    public SearchResult search(SearchRequest request) {
        Objects.requireNonNull(request, "request");
        try (ColumnPipeline pipeline = Objects.requireNonNull(
                pipelines.get(), "pipeline")) {
            pipeline.setExperiment(request.context().experiment())
                    .compile()
                    .feed(SearchFeed.of(inputs, request))
                    .compute();

            List<Row> rows = pipeline.fetch(FetchStream.of(
                    inputs.goodsId(),
                    scores.adjustedScore(),
                    topK.selected()));

            List<ScoredGoods> selected = new ArrayList<>();
            for (Row row : rows) {
                if (row.get(SELECTED_COLUMN).asLong() == 1L) {
                    selected.add(new ScoredGoods(
                            row.get(GOODS_ID_COLUMN).asLong(),
                            row.get(SCORE_COLUMN).asDouble()));
                }
            }
            selected.sort(Comparator
                    .comparingDouble(ScoredGoods::score).reversed());

            List<RankedGoods> ranked = new ArrayList<>(selected.size());
            for (int i = 0; i < selected.size(); i++) {
                ScoredGoods goods = selected.get(i);
                ranked.add(new RankedGoods(i + 1, goods.goodsId(), goods.score()));
            }

            return new SearchResult(
                    request.context(),
                    request.goods().size(),
                    ranked,
                    pipeline.fetchTensor(retarget.retargetAd()),
                    pipeline.fetchTensor(delay.delay()),
                    pipeline.stats());
        }
    }

    private record ScoredGoods(long goodsId, double score) {
    }
}
