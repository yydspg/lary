// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** int64 element-wise multiplication. */
public final class LongMultiTensor extends BinaryTensorOp {

    public LongMultiTensor(List<String> inputs, List<String> outputs) {
        super("long_multi", inputs, outputs, DType.INT64);
    }
    public LongMultiTensor(String[] inputs, String[] outputs) {
        super("long_multi", inputs, outputs, DType.INT64);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.mul(a, b);
    }
}
