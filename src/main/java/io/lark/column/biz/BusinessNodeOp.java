// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.exec.FallibleTensorOp;
import io.lark.column.exec.OpContext;
import io.lark.column.exec.TensorOp;

import java.util.List;
import java.util.Objects;

/** Hidden adapter: business {@link ColumnNode} → execution {@code OpTensor}. */
final class BusinessNodeOp implements FallibleTensorOp {

    private final ColumnNode node;
    private final List<String> inputs;
    private final List<String> outputs;

    BusinessNodeOp(ColumnNode node) {
        this.node = Objects.requireNonNull(node, "node");
        this.inputs = List.copyOf(node.inputs());
        this.outputs = List.copyOf(node.outputs());
    }

    ColumnNode node() {
        return node;
    }

    @Override
    public String opType() {
        return "business";
    }

    @Override
    public List<String> inputs() {
        return inputs;
    }

    @Override
    public List<String> outputs() {
        return outputs;
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
