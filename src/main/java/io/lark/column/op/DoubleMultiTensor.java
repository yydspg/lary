// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** float64 element-wise multiplication. */
public final class DoubleMultiTensor extends BinaryTensorOp {

    public DoubleMultiTensor(List<String> inputs, List<String> outputs) {
        super("double_multi", inputs, outputs, DType.FLOAT64);
    }
    public DoubleMultiTensor(String[] inputs, String[] outputs) {
        super("double_multi", inputs, outputs, DType.FLOAT64);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.mul(a, b);
    }
}
