// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.FallibleTensorKernel;
import io.lark.column.tensor.KernelContext;
import io.lark.column.tensor.TensorKernel;

import java.util.Objects;

/**
 * Kernel adapter for a business {@link ColumnNode}.
 *
 * <p>Business nodes are allowed to read/write their typed columns through
 * {@link ColumnContext}; all arithmetic remains in the node implementation or
 * in lower-level kernels. This adapter is the only bridge from the kernel layer
 * back into business orchestration.
 */
final class BusinessNodeKernel implements TensorKernel, FallibleTensorKernel {

    private final ColumnNode node;

    BusinessNodeKernel(ColumnNode node) {
        this.node = Objects.requireNonNull(node, "node");
    }

    @Override
    public void compute(KernelContext ctx) {
        node.compute(new ColumnContext(ctx.store(), ctx.experiment()));
    }

    @Override
    public boolean fallback(KernelContext ctx, Throwable error) {
        return node.fallback(new ColumnContext(ctx.store(), ctx.experiment()), error);
    }
}
