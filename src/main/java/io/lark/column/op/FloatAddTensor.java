// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.data.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** float32 element-wise addition. */
public final class FloatAddTensor extends BinaryTensorOp {

    public FloatAddTensor(List<String> inputs, List<String> outputs) {
        super("float_add", inputs, outputs, DType.FLOAT32);
    }
    public FloatAddTensor(String[] inputs, String[] outputs) {
        super("float_add", inputs, outputs, DType.FLOAT32);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.add(a, b);
    }
}
