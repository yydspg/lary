// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.backend;

import io.lark.column.DType;
import io.lark.column.exec.OpSpec;
import io.lark.column.exec.TensorOp;
import io.lark.column.exec.TensorOps;

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
    public TensorOp createOp(OpSpec spec) {
        return TensorOps.create(spec);
    }
}
