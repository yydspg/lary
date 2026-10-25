// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.examples.recsys.columns.Clicks;
import io.lark.examples.recsys.columns.Ctr;
import io.lark.examples.recsys.columns.Impressions;

/** Click-through rate: clicks / impressions. */
public final class CtrNode extends BinaryNode<Clicks, Impressions, Ctr> {

    public CtrNode(Clicks clicks, Impressions impressions) {
        super(clicks, impressions, Ctr.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor clicks, Tensor impressions) {
        return Ratios.divide(clicks, impressions);
    }
}
