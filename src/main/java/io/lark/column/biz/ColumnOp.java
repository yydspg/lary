// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.TensorKernel;

import java.util.Objects;

/**
 * Business-facing computation op.
 *
 * <p>Modules use {@link ColumnOps} (or a domain-specific op factory) to
 * describe what a node computes; they do not receive a {@code Tensor} or
 * {@code TensorKernel}. The kernel is an implementation detail of the op.
 */
public final class ColumnOp<O> {

    private final Column<O> output;
    private final TensorKernel kernel;

    /**
     * Extension point for custom domain ops. Normal business modules should
     * use {@link ColumnOps} helpers instead of constructing kernels directly.
     */
    public ColumnOp(Column<O> output, TensorKernel kernel) {
        this.output = Objects.requireNonNull(output, "output");
        this.kernel = Objects.requireNonNull(kernel, "kernel");
    }

    Column<O> output() {
        return output;
    }

    TensorKernel kernel() {
        return kernel;
    }
}
