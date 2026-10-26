// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.data.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** float32 element-wise multiplication. */
public final class FloatMultiTensor extends BinaryTensorOp {

    public FloatMultiTensor(List<String> inputs, List<String> outputs) {
        super("float_multi", inputs, outputs, DType.FLOAT32);
    }
    public FloatMultiTensor(String[] inputs, String[] outputs) {
        super("float_multi", inputs, outputs, DType.FLOAT32);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.mul(a, b);
    }
}
