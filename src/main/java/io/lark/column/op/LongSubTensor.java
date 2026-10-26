// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.data.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** int64 element-wise subtraction. */
public final class LongSubTensor extends BinaryTensorOp {

    public LongSubTensor(List<String> inputs, List<String> outputs) {
        super("long_sub", inputs, outputs, DType.INT64);
    }
    public LongSubTensor(String[] inputs, String[] outputs) {
        super("long_sub", inputs, outputs, DType.INT64);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.sub(a, b);
    }
}
