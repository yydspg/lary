// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** float64 element-wise addition. */
public final class DoubleAddTensor extends BinaryTensorOp {

    public DoubleAddTensor(List<String> inputs, List<String> outputs) {
        super("double_add", inputs, outputs, DType.FLOAT64);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.add(a, b);
    }
}
