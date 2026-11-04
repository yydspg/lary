// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.column.biz.Column;
import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnOperator;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FetchStream;
import io.lark.column.biz.F;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.NumExpr;
import io.lark.column.biz.op.MulAddOp;
import io.lark.column.biz.op.MulOp;
import io.lark.column.biz.op.OneMinusOp;
import io.lark.column.biz.op.RatioOp;
import io.lark.column.biz.op.ScaleOp;
import io.lark.column.biz.op.TopKOp;
import io.lark.column.context.ColumnRunContext;
import io.lark.column.data.Row;
import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorShape;

import java.util.List;
import java.util.Map;

/**
 * The whole search graph in one module: schema, operators, formula, feed and
 * typed execution.
 *
 * <p>This is the only business graph class in the demo. The request/row model
 * lives in {@link SearchDemo}; the typed output lives in {@link SearchResult}.
 */
@ColumnComponent
public final class SearchModule extends ColumnModule {

    // ---- schema ---------------------------------------------------------

    private static final Column<Long> PV_ID = new Column<>("pv_id", DType.INT64);
    private static final Column<Long> GOODS_ID =
            new Column<>("goods_id", DType.INT64);
    private static final Column<Long> IMPRESSIONS =
            new Column<>("impressions", DType.INT64);
    private static final Column<Long> CLICKS =
            new Column<>("clicks", DType.INT64);
    private static final Column<Long> ORDERS =
            new Column<>("orders", DType.INT64);
    private static final Column<Long> GMV_FEN =
            new Column<>("gmv_fen", DType.INT64);
    private static final Column<Long> COUPON_COST_FEN =
            new Column<>("coupon_cost_fen", DType.INT64);
    private static final Column<Long> BUY_AGAIN_ORDERS =
            new Column<>("buy_again_orders", DType.INT64);
    private static final Column<Long> DELAYED_ORDERS =
            new Column<>("delayed_orders", DType.INT64);
    private static final Column<Long> DELAYED_GMV_FEN =
            new Column<>("delayed_gmv_fen", DType.INT64);
    private static final Column<Long> RETARGET_CLICKS =
            new Column<>("retarget_clicks", DType.INT64);
    private static final Column<Long> RETARGET_COST_FEN =
            new Column<>("retarget_cost_fen", DType.INT64);
    private static final Column<Double> PERTURBATION =
            new Column<>("perturbation", DType.FLOAT64);
    private static final Column<Double> ORIGINAL_SCORE =
            new Column<>("original_score", DType.FLOAT64);

    private static final Column<Double> CTR = new Column<>("ctr", DType.FLOAT64);
    private static final Column<Double> CVR = new Column<>("cvr", DType.FLOAT64);
    private static final Column<Double> CTCVR =
            new Column<>("ctcvr", DType.FLOAT64);
    private static final Column<Double> GMV = new Column<>("gmv", DType.FLOAT64);
    private static final Column<Double> BUYAGAIN =
            new Column<>("buyagain", DType.FLOAT64);
    private static final Column<Double> REBUY_SAME_LOSS =
            new Column<>("rebuy_same_loss", DType.FLOAT64);
    private static final Column<Double> COUPON_COST =
            new Column<>("coupon_cost", DType.FLOAT64);
    private static final Column<Double> COUPON_ROI =
            new Column<>("couponroi", DType.FLOAT64);
    private static final Column<Double> RETARGET_AD =
            new Column<>("retargetAd", DType.FLOAT64);
    private static final Column<Double> DELAY =
            new Column<>("delay", DType.FLOAT64);
    private static final Column<Double> RANKING_SCORE =
            new Column<>("ranking_score", DType.FLOAT64);
    private static final Column<Double> ADJUSTED_SCORE =
            new Column<>("adjusted_score", DType.FLOAT64);
    private static final Column<Long> SELECTED =
            new Column<>("selected", DType.INT64);

    // ---- streams (public: the demo reads them directly) ------------------

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
    public ColumnStream<Double> rankingScore;
    public ColumnStream<Double> adjustedScore;
    public ColumnStream<Long> selected;
    public ColumnStream<Double> retargetAd;
    public ColumnStream<Double> delay;

    private final int topK;
    private final int workers;

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
    protected void buildGraph() {
        pvId = feed(PV_ID);
        goodsId = feed(GOODS_ID);
        impressions = feed(IMPRESSIONS);
        clicks = feed(CLICKS);
        orders = feed(ORDERS);
        gmvFen = feed(GMV_FEN);
        couponCostFen = feed(COUPON_COST_FEN);
        buyAgainOrders = feed(BUY_AGAIN_ORDERS);
        delayedOrders = feed(DELAYED_ORDERS);
        delayedGmvFen = feed(DELAYED_GMV_FEN);
        retargetClicks = feed(RETARGET_CLICKS);
        retargetCostFen = feed(RETARGET_COST_FEN);
        perturbation = feed(PERTURBATION);
        originalScore = feed(ORIGINAL_SCORE);

        ctr = node(CTR, RatioOp.class, clicks, impressions);
        cvr = node(CVR, RatioOp.class, orders, clicks);
        ctcvr = node(CTCVR, MulOp.class, ctr, cvr);
        gmv = node(GMV, new ScaleOp(100.0), gmvFen);
        buyAgain = node(BUYAGAIN, RatioOp.class, buyAgainOrders, orders);
        rebuyLoss = node(REBUY_SAME_LOSS, OneMinusOp.class, buyAgain);
        ColumnStream<Double> couponCost = node(COUPON_COST,
                new ScaleOp(100.0), couponCostFen);
        couponRoi = node(COUPON_ROI, RatioOp.class, gmv, couponCost);
        retargetAd = node(RETARGET_AD, new RetargetAdOp(),
                retargetClicks, clicks, retargetCostFen, gmvFen);
        delay = node(DELAY, new DelayOp(),
                delayedOrders, orders, delayedGmvFen, gmvFen);

        // The only formula in the demo: rerank score with an experiment layer.
        rankingScore = formula(RANKING_SCORE, rankingFormula());
        adjustedScore = node(ADJUSTED_SCORE, MulAddOp.class,
                rankingScore, perturbation, originalScore);
        selected = node(SELECTED, new TopKOp(topK), adjustedScore, pvId);

        output(pvId, goodsId, ctr, cvr, ctcvr, gmv, buyAgain, rebuyLoss, couponRoi,
                rankingScore, adjustedScore, selected, retargetAd, delay);
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

    /** Run one request with a fresh pipeline. */
    public SearchResult search(SearchDemo.SearchRequest request) {
        try (ColumnPipeline pipeline = new ColumnPipeline(workers)) {
            pipeline.add(this);
            return search(pipeline, request);
        }
    }

    /** Run one request on a caller-owned pipeline (Spring uses this form). */
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
        List<SearchResult.RankedGoods> withRanks = new java.util.ArrayList<>(
                ranked.size());
        for (int i = 0; i < ranked.size(); i++) {
            SearchResult.RankedGoods goods = ranked.get(i);
            withRanks.add(new SearchResult.RankedGoods(
                    i + 1, goods.goodsId(), goods.adjustedScore()));
        }

        return new SearchResult(
                request.context(),
                request.goods().size(),
                withRanks,
                pipeline.fetchTensor(retargetAd),
                pipeline.fetchTensor(delay),
                pipeline.stats());
    }

    public FeedStream feedStream(SearchDemo.SearchRequest request) {
        return feedStream(SearchDemo.rows(request));
    }

    public FeedStream feedStream(List<Row> rows) {
        return FeedStream.of(feedStreams(), rows);
    }

    ColumnStream<?>[] feedStreams() {
        return new ColumnStream<?>[]{
                pvId, goodsId, impressions, clicks, orders, gmvFen,
                couponCostFen, buyAgainOrders, delayedOrders, delayedGmvFen,
                retargetClicks, retargetCostFen, perturbation, originalScore};
    }

    // ---- domain operators ----------------------------------------------

    private static final class RetargetAdOp implements ColumnOperator {

        @Override
        public int arity() {
            return 4;
        }

        @Override
        public Tensor apply(Tensor... inputs) {
            Tensor clickShare = RatioOp.divide(inputs[0], inputs[1]);
            Tensor costShare = RatioOp.divide(inputs[2], inputs[3]);
            Tensor roi = RatioOp.divide(inputs[3], inputs[2]);
            int rows = clickShare.size();
            double[] click = clickShare.doubleData();
            double[] cost = costShare.doubleData();
            double[] returnOnAd = roi.doubleData();
            double[] values = new double[rows * 3];
            for (int i = 0; i < rows; i++) {
                values[i * 3] = click[i];
                values[i * 3 + 1] = cost[i];
                values[i * 3 + 2] = returnOnAd[i];
            }
            return Tensor.fromDoubles(TensorShape.of(rows, 3), values);
        }
    }

    private static final class DelayOp implements ColumnOperator {

        @Override
        public int arity() {
            return 4;
        }

        @Override
        public Tensor apply(Tensor... inputs) {
            Tensor orderDelay = RatioOp.divide(inputs[0], inputs[1]);
            Tensor gmvDelay = RatioOp.divide(inputs[2], inputs[3]);
            int rows = orderDelay.size();
            double[] order = orderDelay.doubleData();
            double[] gmv = gmvDelay.doubleData();
            double[] values = new double[rows * 4];
            for (int i = 0; i < rows; i++) {
                values[i * 4] = 1.0 - order[i];
                values[i * 4 + 1] = order[i];
                values[i * 4 + 2] = 1.0 - gmv[i];
                values[i * 4 + 3] = gmv[i];
            }
            return Tensor.fromDoubles(TensorShape.of(rows, 2, 2), values);
        }
    }
}
