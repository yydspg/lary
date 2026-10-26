// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.RatioNode;
import io.lark.examples.search.node.ScaleNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Coupon return on investment, wired from GMV. */
public final class CouponRoiModule extends ColumnModule {

    private final RatioNode couponRoi;

    public CouponRoiModule(GmvModule gmv) {
        ScaleNode couponCost = node(
                new ScaleNode(SearchMetrics.COUPON_COST, 100.0),
                feed(SearchMetrics.COUPON_COST_FEN));
        couponRoi = node(new RatioNode(SearchMetrics.COUPON_ROI),
                gmv.published(SearchMetrics.GMV), couponCost.out());
        dependsOn(GmvModule.class);
        output(couponRoi.out());
    }

    public ColumnStream<Double> couponRoi() {
        return couponRoi.out();
    }
}
