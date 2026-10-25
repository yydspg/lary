// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.op.FallibleOpTensor;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpTensor;

import java.util.Objects;

/** Hidden adapter: business {@link ColumnNode} → tensor operation. */
final class BusinessNodeOp extends OpTensor implements FallibleOpTensor {

    private final ColumnNode node;

    BusinessNodeOp(ColumnNode node) {
        super("business", node.inputs(), node.outputs());
        this.node = Objects.requireNonNull(node, "node");
    }

    ColumnNode node() {
        return node;
    }

    @Override
    public void compute(OpContext ctx) {
        node.execute(new ColumnContext(ctx.store()));
    }

    @Override
    public boolean fallback(OpContext ctx, Throwable error) {
        return node.fallback(new ColumnContext(ctx.store()), error);
    }
}
