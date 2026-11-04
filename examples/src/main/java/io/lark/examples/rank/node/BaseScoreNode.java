// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.rank.node;

import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.ModuleNode;
import io.lark.column.biz.schema.Column;
import io.lark.column.compute.Compute;
import io.lark.column.context.ExperimentContext;
import io.lark.column.tensor.Tensor;

/**
 * Business base score used by the ranking demo.
 *
 * <p>The weighted formula and experiment coefficients are business rules, so
 * they live in this node rather than in a generic operator.
 */
public final class BaseScoreNode extends ModuleNode<Double> {

    private final ColumnStream<Double> ctr;
    private final ColumnStream<Double> cvr;
    private final ColumnStream<Double> gmv;
    private final ColumnStream<Double> quality;
    private final ColumnStream<Double> price;

    public BaseScoreNode(Column<Double> output,
                         ColumnStream<Double> ctr,
                         ColumnStream<Double> cvr,
                         ColumnStream<Double> gmv,
                         ColumnStream<Double> quality,
                         ColumnStream<Double> price) {
        super(output, ctr, cvr, gmv, quality, price);
        this.ctr = ctr;
        this.cvr = cvr;
        this.gmv = gmv;
        this.quality = quality;
        this.price = price;
    }

    @Override
    public void compute(ColumnContext ctx) {
        ExperimentContext experiment = ctx.experiment();
        Tensor score = Compute.mulScalar(ctx.read(ctr),
                experiment.param("rank.ctr_w", 100.0));
        score = Compute.add(score, Compute.mulScalar(ctx.read(cvr),
                experiment.param("rank.cvr_w", 50.0)));
        score = Compute.add(score, Compute.mulScalar(ctx.read(gmv),
                experiment.param("rank.gmv_w", 0.01)));
        score = Compute.add(score, Compute.mulScalar(ctx.read(quality),
                experiment.param("rank.quality_w", 2.0)));
        score = Compute.sub(score, Compute.mulScalar(ctx.read(price),
                experiment.param("rank.price_w", 0.001)));
        ctx.write(out(), score);
    }
}
