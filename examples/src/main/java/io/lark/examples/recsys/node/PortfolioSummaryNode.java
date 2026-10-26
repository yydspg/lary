// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.data.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnStream;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.schema.RecSysMetrics;

/** Aggregate commercial metrics across all rows. */
public final class PortfolioSummaryNode extends ColumnNode {

    private final ColumnStream<Double> gmv;
    private final ColumnStream<Double> couponCost;
    private final ColumnStream<Long> buyAgainOrders;
    private final ColumnStream<Double> totalGmv =
            fetch(RecSysMetrics.TOTAL_GMV);
    private final ColumnStream<Double> totalCouponCost =
            fetch(RecSysMetrics.TOTAL_COUPON_COST);
    private final ColumnStream<Double> overallRoi =
            fetch(RecSysMetrics.OVERALL_ROI);
    private final ColumnStream<Double> overallBuyAgainCost =
            fetch(RecSysMetrics.OVERALL_BUYAGAIN_COST);

    public PortfolioSummaryNode(ColumnStream<Double> gmv,
                                ColumnStream<Double> couponCost,
                                ColumnStream<Long> buyAgainOrders) {
        super(gmv, couponCost, buyAgainOrders);
        this.gmv = gmv;
        this.couponCost = couponCost;
        this.buyAgainOrders = buyAgainOrders;
    }

    public ColumnStream<Double> totalGmv() {
        return totalGmv;
    }

    public ColumnStream<Double> totalCouponCost() {
        return totalCouponCost;
    }

    public ColumnStream<Double> overallRoi() {
        return overallRoi;
    }

    public ColumnStream<Double> overallBuyAgainCost() {
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
