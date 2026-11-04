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

    // ---- streams --------------------------------------------------------

    public ColumnStream<Long> pvId;
    public ColumnStream<Long> goodsId;
    public ColumnStream<Long> impressions;
    public ColumnStream<Long> clicks;
    public ColumnStream<Long> orders;
    public ColumnStream<Long> gmvFen;
    public ColumnStream<Long> couponCostFen;
    public ColumnStream<Long> buyAgainOrders;
    public ColumnStream<Long> delayedOrders;
    public ColumnStream<Long> delayedGmvFen;
    public ColumnStream<Long> retargetClicks;
    public ColumnStream<Long> retargetCostFen;
    public ColumnStream<Double> perturbation;
    public ColumnStream<Double> originalScore;

    public ColumnStream<Double> ctr;
    public ColumnStream<Double> cvr;
    public ColumnStream<Double> ctcvr;
    public ColumnStream<Double> gmv;
    public ColumnStream<Double> buyAgain;
    public ColumnStream<Double> rebuyLoss;
    public ColumnStream<Double> couponRoi;
    public ColumnStream<Double> retargetAd;
    public ColumnStream<Double> delay;
    public ColumnStream<Double> rankingScore;
    public ColumnStream<Double> adjustedScore;
    public ColumnStream<Long> selected;

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
        pvId = feed(PV_ID.longColumn(), SearchDemo.SearchRow::pvId);
        goodsId = feed(GOODS_ID.longColumn(), row -> row.goods().goodsId());
        impressions = feed(IMPRESSIONS.longColumn(), row -> row.goods().impressions());
        clicks = feed(CLICKS.longColumn(), row -> row.goods().clicks());
        orders = feed(ORDERS.longColumn(), row -> row.goods().orders());
        gmvFen = feed(GMV_FEN.longColumn(), row -> row.goods().gmvFen());
        couponCostFen = feed(COUPON_COST_FEN.longColumn(),
                row -> row.goods().couponCostFen());
        buyAgainOrders = feed(BUY_AGAIN_ORDERS.longColumn(),
                row -> row.goods().buyAgainOrders());
        delayedOrders = feed(DELAYED_ORDERS.longColumn(),
                row -> row.goods().delayedOrders());
        delayedGmvFen = feed(DELAYED_GMV_FEN.longColumn(),
                row -> row.goods().delayedGmvFen());
        retargetClicks = feed(RETARGET_CLICKS.longColumn(),
                row -> row.goods().retargetClicks());
        retargetCostFen = feed(RETARGET_COST_FEN.longColumn(),
                row -> row.goods().retargetCostFen());
        perturbation = feed(PERTURBATION.doubleColumn(), row -> row.goods().perturbation());
        originalScore = feed(ORIGINAL_SCORE.doubleColumn(),
                row -> row.goods().originalScore());
    }

    @Override
    protected void buildNodes(ColumnGraphContext context) {
        ctr = node(CTR.doubleColumn(), RatioOp.class, clicks, impressions);
        cvr = node(CVR.doubleColumn(), RatioOp.class, orders, clicks);
        ctcvr = node(CTCVR.doubleColumn(), MulOp.class, ctr, cvr);
        gmv = node(GMV.doubleColumn(), new ScaleOp(100.0), gmvFen);
        buyAgain = node(BUYAGAIN.doubleColumn(), RatioOp.class, buyAgainOrders, orders);
        rebuyLoss = node(REBUY_SAME_LOSS.doubleColumn(), OneMinusOp.class, buyAgain);
        ColumnStream<Double> couponCost = node(new ScaleOp(100.0),
                couponCostFen);
        couponRoi = node(COUPON_ROI.doubleColumn(), RatioOp.class, gmv, couponCost);

        // Shaped tensors are still pure operator wiring: ratio/one-minus
        // columns first, then one framework StackOp per output.
        ColumnStream<Double> clickShare = node(
                RatioOp.class, retargetClicks, clicks);
        var costShare = node(RatioOp.class, retargetCostFen, gmvFen);
        ColumnStream<Double> roi = node(
                RatioOp.class, gmvFen, retargetCostFen);
        ColumnStream<Double> scaledRoi = node(
                new ExperimentScaleOp("rerank.roi_w", 1.0), roi);
        roi = node(new VariantSwitchOp("roi_weight", "on"),
                roi, scaledRoi);
        retargetAd = node(RETARGET_AD.doubleColumn(), new StackOp(3),
                clickShare, costShare, roi);

        ColumnStream<Double> orderDelay = node(
                RatioOp.class, delayedOrders, orders);
        ColumnStream<Double> gmvDelay = node(
                RatioOp.class, delayedGmvFen, gmvFen);
        ColumnStream<Double> orderOnTime = node(
                OneMinusOp.class, orderDelay);
        ColumnStream<Double> gmvOnTime = node(
                OneMinusOp.class, gmvDelay);
        delay = node(DELAY.doubleColumn(), new StackOp(2, 2),
                orderOnTime, orderDelay, gmvOnTime, gmvDelay);

        rankingScore = formula(RANKING_SCORE.doubleColumn(), rankingFormula());
        ColumnStream<Double> weightedScore = node(
                new ExperimentScaleOp("rerank.exp_w", 1.0), rankingScore);
        adjustedScore = node(ADJUSTED_SCORE.doubleColumn(), MulAddOp.class,
                weightedScore, perturbation, originalScore);
        SearchGraphConfig config = context.get(SearchGraphConfig.class);
        int effectiveTopK = (int) context.experiment().param("rerank.top_k",
                config == null ? topK : config.topK());
        selected = node(SELECTED.longColumn(), new TopKOp(effectiveTopK),
                adjustedScore, pvId);
    }

    @Override
    protected void buildFetches(ColumnGraphContext context) {
        output(pvId, goodsId, ctr, cvr, ctcvr, gmv, buyAgain, rebuyLoss,
                couponRoi, retargetAd, delay, rankingScore, adjustedScore,
                selected);
    }

    private NumExpr rankingFormula() {
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
        pipeline.setExperiment(request.context().experiment())
                .setRunContext(ColumnRunContext.of(request.context()))
                .execute(feedStream(request));

        List<Row> rows = pipeline.fetch(FetchStream.of(
                goodsId, adjustedScore, selected));
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
                pipeline.fetchTensor(retargetAd),
                pipeline.fetchTensor(delay),
                pipeline.stats());
    }

    public FeedStream feedStream(SearchDemo.SearchRequest request) {
        return feedStream(SearchDemo.rows(request));
    }

}
