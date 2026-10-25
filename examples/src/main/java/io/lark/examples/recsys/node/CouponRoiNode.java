// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.examples.recsys.columns.CouponCost;
import io.lark.examples.recsys.columns.CouponRoi;
import io.lark.examples.recsys.columns.Gmv;

/** Coupon ROI: GMV / coupon spend. */
public final class CouponRoiNode
        extends BinaryNode<Gmv, CouponCost, CouponRoi> {

    public CouponRoiNode(Gmv gmv, CouponCost cost) {
        super(gmv, cost, CouponRoi.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor gmv, Tensor cost) {
        return Ratios.divide(gmv, cost);
    }
}
