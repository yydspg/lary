// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FormulaConfig;
import io.lark.column.biz.NumExpr;
import io.lark.examples.search.SearchFormulaConfig;
import io.lark.examples.search.schema.SearchMetrics;

import java.util.Map;

/**
 * Rerank formula. Experiment parameters and variant branches are evaluated per
 * request without rebuilding the graph.
 */
@ColumnComponent
public final class RankingScoreModule extends ColumnModule {

    private final CtrModule ctr;
    private final CvrModule cvr;
    private final GmvModule gmv;
    private final CouponRoiModule couponRoi;
    private final RebuySameLossModule rebuy;
    private ColumnStream<Double> score;

    public RankingScoreModule(CtrModule ctr, CvrModule cvr, GmvModule gmv,
                              CouponRoiModule couponRoi,
                              RebuySameLossModule rebuy) {
        this.ctr = ctr;
        this.cvr = cvr;
        this.gmv = gmv;
        this.couponRoi = couponRoi;
        this.rebuy = rebuy;
    }

    @Override
    protected void define() {
        ColumnStream<Double> ctrColumn = ctr.published(SearchMetrics.CTR);
        ColumnStream<Double> cvrColumn = cvr.published(SearchMetrics.CVR);
        ColumnStream<Double> gmvColumn = gmv.published(SearchMetrics.GMV);
        ColumnStream<Double> couponColumn =
                couponRoi.published(SearchMetrics.COUPON_ROI);
        ColumnStream<Double> rebuyColumn =
                rebuy.published(SearchMetrics.REBUY_SAME_LOSS);

        // The formula lives in external configuration; only the typed column
        // handles are supplied by the module.
        FormulaConfig config = SearchFormulaConfig.ranking();
        NumExpr scoreFormula = config.compileNum(Map.of(
                SearchMetrics.CTR.name(), ctrColumn,
                SearchMetrics.CVR.name(), cvrColumn,
                SearchMetrics.GMV.name(), gmvColumn,
                SearchMetrics.COUPON_ROI.name(), couponColumn,
                SearchMetrics.REBUY_SAME_LOSS.name(), rebuyColumn));
        score = formula(SearchMetrics.RANKING_SCORE, scoreFormula);

        dependsOn(CtrModule.class, CvrModule.class, GmvModule.class,
                CouponRoiModule.class, RebuySameLossModule.class);
        output(score);
    }

    public ColumnStream<Double> score() {
        return score;
    }
}
