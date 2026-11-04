// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.F;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.column.biz.NumExpr;
import io.lark.column.biz.op.MulAddOp;
import io.lark.column.biz.op.MulOp;
import io.lark.column.biz.op.OneMinusOp;
import io.lark.column.biz.op.ExperimentScaleOp;
import io.lark.column.biz.op.RatioOp;
import io.lark.column.biz.op.ScaleOp;
import io.lark.column.biz.op.StackOp;
import io.lark.column.biz.op.VariantSwitchOp;
import io.lark.column.biz.op.TopKOp;
import io.lark.column.context.ColumnGraphContext;
import io.lark.column.context.ColumnRunContext;
import io.lark.column.data.Row;

import java.util.List;
import java.util.Map;

import static io.lark.examples.search.SearchSchema.*;

/**
 * The whole search graph in one module: schema, operators, formula, feed and
 * typed execution.
 */
@ColumnComponent
public final class SearchModule extends ColumnModule<SearchDemo.SearchRow> {

    private final int topK;
    private final int workers;

    /** Optional business graph config carried by {@link ColumnGraphContext}. */
    public record SearchGraphConfig(int topK) {

        public SearchGraphConfig {
            if (topK <= 0) {
                throw new IllegalArgumentException("topK must be positive");
            }
        }
    }

    public SearchModule() {
        this(30, 4);
    }

    public SearchModule(int topK) {
        this(topK, 4);
    }

    public SearchModule(int topK, int workers) {
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be positive");
        }
        this.topK = topK;
        this.workers = Math.max(1, workers);
    }

    // ---- graph ----------------------------------------------------------

    @Override
    protected void buildFeeds(ColumnGraphContext context) {
        feed(PV_ID.longColumn(), SearchDemo.SearchRow::pvId);
        feed(GOODS_ID.longColumn(), row -> row.goods().goodsId());
        feed(IMPRESSIONS.longColumn(), row -> row.goods().impressions());
        feed(CLICKS.longColumn(), row -> row.goods().clicks());
        feed(ORDERS.longColumn(), row -> row.goods().orders());
        feed(GMV_FEN.longColumn(), row -> row.goods().gmvFen());
        feed(COUPON_COST_FEN.longColumn(),
                row -> row.goods().couponCostFen());
        feed(BUY_AGAIN_ORDERS.longColumn(),
                row -> row.goods().buyAgainOrders());
        feed(DELAYED_ORDERS.longColumn(),
                row -> row.goods().delayedOrders());
        feed(DELAYED_GMV_FEN.longColumn(),
                row -> row.goods().delayedGmvFen());
        feed(RETARGET_CLICKS.longColumn(),
                row -> row.goods().retargetClicks());
        feed(RETARGET_COST_FEN.longColumn(),
                row -> row.goods().retargetCostFen());
        feed(PERTURBATION.doubleColumn(), row -> row.goods().perturbation());
        feed(ORIGINAL_SCORE.doubleColumn(),
                row -> row.goods().originalScore());
    }

    @Override
    protected void buildNodes(ColumnGraphContext context) {
        var pvId = required(PV_ID.longColumn());
        var impressions = required(IMPRESSIONS.longColumn());
        var clicks = required(CLICKS.longColumn());
        var orders = required(ORDERS.longColumn());
        var gmvFen = required(GMV_FEN.longColumn());
        var couponCostFen = required(COUPON_COST_FEN.longColumn());
        var buyAgainOrders = required(BUY_AGAIN_ORDERS.longColumn());
        var delayedOrders = required(DELAYED_ORDERS.longColumn());
        var delayedGmvFen = required(DELAYED_GMV_FEN.longColumn());
        var retargetClicks = required(RETARGET_CLICKS.longColumn());
        var retargetCostFen = required(RETARGET_COST_FEN.longColumn());
        var perturbation = required(PERTURBATION.doubleColumn());
        var originalScore = required(ORIGINAL_SCORE.doubleColumn());

        var ctr = node(CTR.doubleColumn(), RatioOp.class, clicks, impressions);
        var cvr = node(CVR.doubleColumn(), RatioOp.class, orders, clicks);
        node(CTCVR.doubleColumn(), MulOp.class, ctr, cvr);
        var gmv = node(GMV.doubleColumn(), new ScaleOp(100.0), gmvFen);
        var buyAgain = node(BUYAGAIN.doubleColumn(),
                RatioOp.class, buyAgainOrders, orders);
        var rebuyLoss = node(REBUY_SAME_LOSS.doubleColumn(),
                OneMinusOp.class, buyAgain);
        var couponCost = node(new ScaleOp(100.0), couponCostFen);
        var couponRoi = node(COUPON_ROI.doubleColumn(),
                RatioOp.class, gmv, couponCost);

        // Shaped tensors are still pure operator wiring: ratio/one-minus
        // columns first, then one framework StackOp per output.
        var clickShare = node(RatioOp.class, retargetClicks, clicks);
        var costShare = node(RatioOp.class, retargetCostFen, gmvFen);
        var roi = node(RatioOp.class, gmvFen, retargetCostFen);
        var scaledRoi = node(
                new ExperimentScaleOp("rerank.roi_w", 1.0), roi);
        roi = node(new VariantSwitchOp("roi_weight", "on"), roi, scaledRoi);
        node(RETARGET_AD.doubleColumn(), new StackOp(3),
                clickShare, costShare, roi);

        var orderDelay = node(RatioOp.class, delayedOrders, orders);
        var gmvDelay = node(RatioOp.class, delayedGmvFen, gmvFen);
        var orderOnTime = node(OneMinusOp.class, orderDelay);
        var gmvOnTime = node(OneMinusOp.class, gmvDelay);
        node(DELAY.doubleColumn(), new StackOp(2, 2),
                orderOnTime, orderDelay, gmvOnTime, gmvDelay);

        var rankingScore = formula(RANKING_SCORE.doubleColumn(),
                rankingFormula(ctr, cvr, gmv, couponRoi, rebuyLoss));
        var weightedScore = node(
                new ExperimentScaleOp("rerank.exp_w", 1.0), rankingScore);
        var adjustedScore = node(ADJUSTED_SCORE.doubleColumn(),
                MulAddOp.class, weightedScore, perturbation, originalScore);
        SearchGraphConfig config = context.get(SearchGraphConfig.class);
        int effectiveTopK = (int) context.experiment().param("rerank.top_k",
                config == null ? topK : config.topK());
        node(SELECTED.longColumn(), new TopKOp(effectiveTopK),
                adjustedScore, pvId);
    }

    @Override
    protected void buildFetches(ColumnGraphContext context) {
        fetch(PV_ID.longColumn(), GOODS_ID.longColumn(),
                CTR.doubleColumn(), CVR.doubleColumn(), CTCVR.doubleColumn(),
                GMV.doubleColumn(), BUYAGAIN.doubleColumn(),
                REBUY_SAME_LOSS.doubleColumn(), COUPON_ROI.doubleColumn(),
                RETARGET_AD.doubleColumn(), DELAY.doubleColumn(),
                RANKING_SCORE.doubleColumn(), ADJUSTED_SCORE.doubleColumn(),
                SELECTED.longColumn());
    }

    private NumExpr rankingFormula(ColumnStream<Double> ctr,
                                   ColumnStream<Double> cvr,
                                   ColumnStream<Double> gmv,
                                   ColumnStream<Double> couponRoi,
                                   ColumnStream<Double> rebuyLoss) {
        NumExpr control = F.sum(
                F.weighted(F.col(ctr), "rerank.ctr_w", 100.0),
                F.weighted(F.col(cvr), "rerank.cvr_w", 50.0),
                F.weighted(F.col(gmv), "rerank.gmv_w", 0.01),
                F.weighted(F.col(couponRoi), "rerank.coupon_roi_w", 0.5),
                F.neg(F.weighted(F.col(rebuyLoss), "rerank.rebuy_w", 0.5)));
        NumExpr couponBoost = F.when(
                F.and(F.variant("rerank", "coupon_boost"),
                        F.gt(F.col(couponRoi), F.val(0.0))),
                F.weighted(F.col(gmv), "rerank.coupon_boost_w", 0.02),
                F.val(0.0));

        return F.chooseVariant("rerank",
                Map.of("control", control,
                        "coupon_boost", control.add(couponBoost)),
                control);
    }

    // ---- execution ------------------------------------------------------

    public SearchResult search(SearchDemo.SearchRequest request) {
        return search(request, ColumnGraphContext.empty());
    }

    public SearchResult search(SearchDemo.SearchRequest request,
                               ColumnGraphContext graphContext) {
        try (ColumnPipeline pipeline = new ColumnPipeline(workers)) {
            pipeline.setGraphContext(graphContext).add(this);
            return search(pipeline, request);
        }
    }

    public SearchResult search(ColumnPipeline pipeline,
                               SearchDemo.SearchRequest request) {
        var required = FetchStream.of(
                required(GOODS_ID.longColumn()),
                required(ADJUSTED_SCORE.doubleColumn()),
                required(SELECTED.longColumn()),
                required(RETARGET_AD.doubleColumn()),
                required(DELAY.doubleColumn()));
        pipeline.setExperiment(request.context().experiment())
                .executeTensors(feedTensorMap(SearchDemo.rows(request)),
                        required,
                        ColumnRunContext.of(request.context()));

        List<Row> rows = pipeline.fetch(List.of(
                GOODS_ID.columnName(), ADJUSTED_SCORE.columnName(),
                SELECTED.columnName()));
        List<SearchResult.RankedGoods> ranked = rows.stream()
                .filter(row -> row.get(2).asLong() == 1L)
                .map(row -> new SearchResult.RankedGoods(
                        0, row.get(0).asLong(), row.get(1).asDouble()))
                .sorted((left, right) -> Double.compare(
                        right.adjustedScore(), left.adjustedScore()))
                .toList();
        List<SearchResult.RankedGoods> withRanks =
                new java.util.ArrayList<>(ranked.size());
        for (int i = 0; i < ranked.size(); i++) {
            SearchResult.RankedGoods goods = ranked.get(i);
            withRanks.add(new SearchResult.RankedGoods(
                    i + 1, goods.goodsId(), goods.adjustedScore()));
        }

        return new SearchResult(request.context(), request.goods().size(),
                withRanks,
                pipeline.fetchTensor(RETARGET_AD.columnName()),
                pipeline.fetchTensor(DELAY.columnName()),
                pipeline.stats());
    }

    public FeedStream feedStream(SearchDemo.SearchRequest request) {
        return feedStream(SearchDemo.rows(request));
    }

}
