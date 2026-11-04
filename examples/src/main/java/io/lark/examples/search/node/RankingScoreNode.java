// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.node;

import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.ModuleNode;
import io.lark.column.biz.schema.Column;
import io.lark.column.compute.Compute;
import io.lark.column.context.ExperimentContext;
import io.lark.column.tensor.Tensor;

/**
 * Business rerank score:
 *
 * <pre>
 * 100*ctr + 50*cvr + 0.01*gmv + 0.5*couponroi - 0.5*rebuy_same_loss
 * </pre>
 *
 * <p>The coupon-boost variant is row-level business logic, so it lives in this
 * node rather than in a generic operator. Weights and the variant are read from
 * the per-run {@link ExperimentContext}; the graph is not rebuilt.
 */
public final class RankingScoreNode extends ModuleNode<Double> {

    private final ColumnStream<Double> ctr;
    private final ColumnStream<Double> cvr;
    private final ColumnStream<Double> gmv;
    private final ColumnStream<Double> couponRoi;
    private final ColumnStream<Double> rebuyLoss;

    public RankingScoreNode(Column<Double> output,
                            ColumnStream<Double> ctr,
                            ColumnStream<Double> cvr,
                            ColumnStream<Double> gmv,
                            ColumnStream<Double> couponRoi,
                            ColumnStream<Double> rebuyLoss) {
        super(output, ctr, cvr, gmv, couponRoi, rebuyLoss);
        this.ctr = ctr;
        this.cvr = cvr;
        this.gmv = gmv;
        this.couponRoi = couponRoi;
        this.rebuyLoss = rebuyLoss;
    }

    @Override
    public void compute(ColumnContext ctx) {
        ExperimentContext experiment = ctx.experiment();
        Tensor score = Compute.mulScalar(ctx.read(ctr),
                experiment.param("rerank.ctr_w", 100.0));
        score = Compute.add(score, Compute.mulScalar(ctx.read(cvr),
                experiment.param("rerank.cvr_w", 50.0)));
        score = Compute.add(score, Compute.mulScalar(ctx.read(gmv),
                experiment.param("rerank.gmv_w", 0.01)));
        score = Compute.add(score, Compute.mulScalar(ctx.read(couponRoi),
                experiment.param("rerank.coupon_roi_w", 0.5)));
        score = Compute.sub(score, Compute.mulScalar(ctx.read(rebuyLoss),
                experiment.param("rerank.rebuy_w", 0.5)));

        if (experiment.is("rerank", "coupon_boost")) {
            Tensor gmvTensor = ctx.read(gmv);
            double[] roi = ctx.read(couponRoi).doubleData();
            double[] gmvValues = gmvTensor.doubleData();
            double weight = experiment.param("rerank.coupon_boost_w", 0.02);
            double[] boost = new double[roi.length];
            for (int i = 0; i < boost.length; i++) {
                if (roi[i] > 0.0) {
                    boost[i] = gmvValues[i] * weight;
                }
            }
            score = Compute.add(score,
                    Tensor.fromDoubles(gmvTensor.shape(), boost));
        }
        ctx.write(out(), score);
    }
}
