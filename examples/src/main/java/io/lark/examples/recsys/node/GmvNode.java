// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.recsys.node;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;
import io.lark.examples.recsys.columns.Gmv;
import io.lark.examples.recsys.columns.GmvFen;

/** Fen to yuan for gross merchandise value. */
public final class GmvNode extends UnaryNode<GmvFen, Gmv> {

    public GmvNode(GmvFen input) {
        super(input, Gmv.class);
    }

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor gmvFen) {
        return Compute.divScalar(gmvFen, 100.0);
    }
}
