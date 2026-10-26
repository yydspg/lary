// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.data.Tensor;
import io.lark.column.compute.Compute;

import java.util.List;

/** float64 division; also the target for all integer division. */
public final class DoubleDivTensor extends BinaryTensorOp {

    public DoubleDivTensor(List<String> inputs, List<String> outputs) {
        super("double_div", inputs, outputs, DType.FLOAT64);
    }
    public DoubleDivTensor(String[] inputs, String[] outputs) {
        super("double_div", inputs, outputs, DType.FLOAT64);
    }

    @Override
    protected Tensor apply(Tensor a, Tensor b) {
        return Compute.div(a, b);
    }
}
