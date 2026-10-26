// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Coupon return on investment, wired from GMV. */
@ColumnComponent
public final class CouponRoiModule extends ColumnModule {

    private final GmvModule gmv;
    private ColumnStream<Double> couponRoi;

    public CouponRoiModule(GmvModule gmv) {
        this.gmv = gmv;
    }

    @Override
    protected void define() {
        ColumnStream<Double> couponCost = node(
                SearchOps.scale(SearchMetrics.COUPON_COST, 100.0),
                feed(SearchMetrics.COUPON_COST_FEN));
        couponRoi = node(SearchOps.ratio(SearchMetrics.COUPON_ROI),
                gmv.published(SearchMetrics.GMV), couponCost);
        dependsOn(GmvModule.class);
        output(couponRoi);
    }

    public ColumnStream<Double> couponRoi() {
        return couponRoi;
    }
}
