// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.tensor.DType;
import io.lark.column.compute.Compute;

import java.util.List;

/** Runtime dispatcher for multiplication; chooses a typed MultiTensor. */
public final class MultiTensor extends OpTensor {

    private final OpTensor[] typed = {
            new IntMultiTensor(inputs(), outputs()),
            new LongMultiTensor(inputs(), outputs()),
            new FloatMultiTensor(inputs(), outputs()),
            new DoubleMultiTensor(inputs(), outputs())
    };

    public MultiTensor(List<String> inputs, List<String> outputs) {
        super("mul", inputs, outputs);
    }
    public MultiTensor(String[] inputs, String[] outputs) {
        super("mul", inputs, outputs);
    }

    @Override
    public void compute(OpContext ctx) {
        DType out = Compute.promote(ctx.input(inputs()[0]).dtype(),
                ctx.input(inputs()[1]).dtype());
        switch (out) {
            case INT32 -> typed[0].compute(ctx);
            case INT64 -> typed[1].compute(ctx);
            case FLOAT32 -> typed[2].compute(ctx);
            default -> typed[3].compute(ctx);
        }
    }
}
