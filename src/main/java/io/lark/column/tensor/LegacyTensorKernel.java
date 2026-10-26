// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.tensor;

import io.lark.column.op.FallibleOpTensor;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpTensor;

import java.util.Objects;

/** Compatibility bridge for pre-kernel {@link OpTensor} instances. */
final class LegacyTensorKernel implements TensorKernel, FallibleTensorKernel {

    private final OpTensor op;

    LegacyTensorKernel(OpTensor op) {
        this.op = Objects.requireNonNull(op, "op");
    }

    @Override
    public void compute(KernelContext ctx) {
        op.compute(new OpContext(ctx.store()));
    }

    @Override
    public boolean fallback(KernelContext ctx, Throwable error) {
        if (op instanceof FallibleOpTensor fallible) {
            return fallible.fallback(new OpContext(ctx.store()), error);
        }
        return false;
    }
}
