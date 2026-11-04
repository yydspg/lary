// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.rank.node;

import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.ModuleNode;
import io.lark.column.biz.schema.Column;
import io.lark.column.compute.Compute;
import io.lark.column.tensor.Tensor;

/** Business penalty rule: {@code base - categoryPenalty - brandPenalty}. */
public final class PenaltyScoreNode extends ModuleNode<Double> {

    private final ColumnStream<Double> base;
    private final ColumnStream<Double> categoryPenalty;
    private final ColumnStream<Double> brandPenalty;

    public PenaltyScoreNode(Column<Double> output,
                            ColumnStream<Double> base,
                            ColumnStream<Double> categoryPenalty,
                            ColumnStream<Double> brandPenalty) {
        super(output, base, categoryPenalty, brandPenalty);
        this.base = base;
        this.categoryPenalty = categoryPenalty;
        this.brandPenalty = brandPenalty;
    }

    @Override
    public void compute(ColumnContext ctx) {
        Tensor score = Compute.sub(ctx.read(base), ctx.read(categoryPenalty));
        score = Compute.sub(score, ctx.read(brandPenalty));
        ctx.write(out(), score);
    }
}
