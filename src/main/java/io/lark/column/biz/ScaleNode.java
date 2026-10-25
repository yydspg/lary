// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.compute.Compute;

/**
 * Business transformation node: multiply a stream by a weight.
 *
 * <pre>{@code
 * ScaleNode weighted = new ScaleNode(score.out, "weighted_score", 0.3);
 * }</pre>
 */
public final class ScaleNode extends ColumnNode {

    private final ColumnStream feed;
    public final ColumnStream out;
    private final double weight;

    public ScaleNode(ColumnStream feed, String outputName, double weight) {
        super(feed);
        this.feed = feed;
        this.out = output(outputName);
        this.weight = weight;
    }

    public ColumnStream out() {
        return out;
    }

    @Override
    public void compute(ColumnContext ctx) {
        ctx.write(out, Compute.mulScalar(ctx.read(feed), weight));
    }
}
