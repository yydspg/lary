// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.columns.BuyAgainOrders;
import io.lark.examples.recsys.columns.CouponCost;
import io.lark.examples.recsys.columns.Gmv;
import io.lark.examples.recsys.columns.OverallBuyAgainCost;
import io.lark.examples.recsys.columns.OverallRoi;
import io.lark.examples.recsys.columns.TotalCouponCost;
import io.lark.examples.recsys.columns.TotalGmv;

/** Aggregate commercial metrics across all rows. */
public final class PortfolioSummaryNode extends ColumnNode {

    private final Gmv gmv;
    private final CouponCost couponCost;
    private final BuyAgainOrders buyAgainOrders;
    private final TotalGmv totalGmv = fetch(TotalGmv.class);
    private final TotalCouponCost totalCouponCost = fetch(TotalCouponCost.class);
    private final OverallRoi overallRoi = fetch(OverallRoi.class);
    private final OverallBuyAgainCost overallBuyAgainCost =
            fetch(OverallBuyAgainCost.class);

    public PortfolioSummaryNode(Gmv gmv, CouponCost couponCost,
                                BuyAgainOrders buyAgainOrders) {
        super(gmv, couponCost, buyAgainOrders);
        this.gmv = gmv;
        this.couponCost = couponCost;
        this.buyAgainOrders = buyAgainOrders;
    }

    public TotalGmv totalGmv() {
        return totalGmv;
    }

    public TotalCouponCost totalCouponCost() {
        return totalCouponCost;
    }

    public OverallRoi overallRoi() {
        return overallRoi;
    }

    public OverallBuyAgainCost overallBuyAgainCost() {
        return overallBuyAgainCost;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor totalGmvValue = Compute.sumTensor(ctx.read(gmv));
        Tensor totalCostValue = Compute.sumTensor(ctx.read(couponCost));
        Tensor repeatOrdersValue = Compute.sumTensor(ctx.read(buyAgainOrders));
        ctx.write(totalGmv, totalGmvValue);
        ctx.write(totalCouponCost, totalCostValue);
        ctx.write(overallRoi, Ratios.divide(totalGmvValue, totalCostValue));
        ctx.write(overallBuyAgainCost,
                Ratios.divide(totalCostValue, repeatOrdersValue));
    }
}
