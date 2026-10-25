// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.schema.RecSysMetrics;

/**
 * Search/ranking weight from the funnel and commercial metrics.
 *
 * <p>This is deliberately a business formula, not a framework concern. A real
 * system would load the coefficients from experiment configuration.
 */
public final class RankingWeightNode extends ColumnNode {

    public static final NodeSchema<Double> SCHEMA =
            NodeSchema.of(RecSysMetrics.WEIGHT);

    private static final double CTR_WEIGHT = 100.0;
    private static final double CVR_WEIGHT = 50.0;
    private static final double GMV_WEIGHT = 0.01;
    private static final double ROI_WEIGHT = 0.5;
    private static final double BUY_AGAIN_COST_PENALTY = 0.5;

    private final ColumnStream<Double> ctr;
    private final ColumnStream<Double> cvr;
    private final ColumnStream<Double> gmv;
    private final ColumnStream<Double> couponRoi;
    private final ColumnStream<Double> buyAgainCost;
    private final ColumnStream<Double> out = fetch(SCHEMA);

    public RankingWeightNode(ColumnStream<Double> ctr,
                             ColumnStream<Double> cvr,
                             ColumnStream<Double> gmv,
                             ColumnStream<Double> couponRoi,
                             ColumnStream<Double> buyAgainCost) {
        super(ctr, cvr, gmv, couponRoi, buyAgainCost);
        this.ctr = ctr;
        this.cvr = cvr;
        this.gmv = gmv;
        this.couponRoi = couponRoi;
        this.buyAgainCost = buyAgainCost;
    }

    public ColumnStream<Double> out() {
        return out;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor score = Compute.add(
                Compute.mulScalar(ctx.read(ctr), CTR_WEIGHT),
                Compute.mulScalar(ctx.read(cvr), CVR_WEIGHT));
        score = Compute.add(score, Compute.mulScalar(ctx.read(gmv), GMV_WEIGHT));
        score = Compute.add(score, Compute.mulScalar(ctx.read(couponRoi), ROI_WEIGHT));
        score = Compute.sub(score,
                Compute.mulScalar(ctx.read(buyAgainCost), BUY_AGAIN_COST_PENALTY));
        ctx.write(out, score);
    }
}
