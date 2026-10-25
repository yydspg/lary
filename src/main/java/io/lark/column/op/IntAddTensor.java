// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** int32 element-wise addition. */
public final class IntAddTensor extends BinaryTensorOp {

    public IntAddTensor(List<String> inputs, List<String> outputs) {
        super("int_add", inputs, outputs, DType.INT32);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.add(a, b);
    }
}
