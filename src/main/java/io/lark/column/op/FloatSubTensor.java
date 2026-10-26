// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** float32 element-wise subtraction. */
public final class FloatSubTensor extends BinaryTensorOp {

    public FloatSubTensor(List<String> inputs, List<String> outputs) {
        super("float_sub", inputs, outputs, DType.FLOAT32);
    }
    public FloatSubTensor(String[] inputs, String[] outputs) {
        super("float_sub", inputs, outputs, DType.FLOAT32);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.sub(a, b);
    }
}
