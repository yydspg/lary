// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.compute.Compute;

/**
 * Business transformation node: convert a numeric unit by a factor.
 *
 * <pre>{@code
 * UnitConvertNode li = new UnitConvertNode(amountFen.out, "amount_li", 10.0);
 * }</pre>
 */
public final class UnitConvertNode extends ColumnNode {

    private final ColumnStream feed;
    public final ColumnStream out;
    private final double factor;

    public UnitConvertNode(ColumnStream feed, String outputName, double factor) {
        super(feed);
        this.feed = feed;
        this.out = output(outputName);
        this.factor = factor;
    }

    public ColumnStream out() {
        return out;
    }

    @Override
    public void compute(ColumnContext ctx) {
        ctx.write(out, Compute.mulScalar(ctx.read(feed), factor));
    }
}
