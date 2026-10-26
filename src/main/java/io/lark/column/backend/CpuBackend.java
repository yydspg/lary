// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.backend;

import io.lark.column.data.DType;
import io.lark.column.kernel.TensorKernel;
import io.lark.column.kernel.TensorKernels;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;

/** CPU backend: all scalar dtypes and Q8_0 are supported. */
public final class CpuBackend implements Backend {

    @Override
    public String name() {
        return "cpu";
    }

    @Override
    public boolean supports(DType dtype) {
        return dtype.isScalar() || dtype.isQuantized();
    }

    @Override
    public TensorKernel createKernel(OpSpec spec) {
        return TensorKernels.create(spec);
    }

    @Override
    @Deprecated
    public OpTensor createOp(OpSpec spec) {
        return io.lark.column.op.TensorOps.create(spec);
    }
}
