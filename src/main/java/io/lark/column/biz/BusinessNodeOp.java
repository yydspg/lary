// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.op.FallibleOpTensor;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpTensor;

import java.util.Objects;

/** Hidden adapter from a business {@link ColumnNode} to the execution layer. */
final class BusinessNodeOp extends OpTensor implements FallibleOpTensor {

    private final ColumnNode node;

    BusinessNodeOp(ColumnNode node) {
        super("business", names(node.feedView()), names(node.fetchView()));
        this.node = Objects.requireNonNull(node, "node");
    }

    private static String[] names(ColumnStream<?>[] streams) {
        String[] names = new String[streams.length];
        for (int i = 0; i < streams.length; i++) {
            names[i] = streams[i].name();
        }
        return names;
    }

    ColumnNode node() {
        return node;
    }

    @Override
    public void compute(OpContext ctx) {
        node.compute(new ColumnContext(ctx.store()));
    }

    @Override
    public boolean fallback(OpContext ctx, Throwable error) {
        return node.fallback(new ColumnContext(ctx.store()), error);
    }
}
