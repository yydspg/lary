// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.data.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** int32 element-wise multiplication. */
public final class IntMultiTensor extends BinaryTensorOp {

    public IntMultiTensor(List<String> inputs, List<String> outputs) {
        super("int_multi", inputs, outputs, DType.INT32);
    }
    public IntMultiTensor(String[] inputs, String[] outputs) {
        super("int_multi", inputs, outputs, DType.INT32);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.mul(a, b);
    }
}
