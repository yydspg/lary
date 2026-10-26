// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Ranking score from funnel, commercial and rebuy metrics. */
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
        score = node(SearchOps.rankingScore(SearchMetrics.RANKING_SCORE),
                ctr.published(SearchMetrics.CTR),
                cvr.published(SearchMetrics.CVR),
                gmv.published(SearchMetrics.GMV),
                couponRoi.published(SearchMetrics.COUPON_ROI),
                rebuy.published(SearchMetrics.REBUY_SAME_LOSS));
        dependsOn(CtrModule.class, CvrModule.class, GmvModule.class,
                CouponRoiModule.class, RebuySameLossModule.class);
        output(score);
    }

    public ColumnStream<Double> score() {
        return score;
    }
}
