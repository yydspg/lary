// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.compute.Compute;

import java.util.List;

/** Runtime dispatcher for multiplication; chooses a typed MultiTensor. */
public final class MultiTensor extends OpTensor {

    public MultiTensor(List<String> inputs, List<String> outputs) {
        super("mul", inputs, outputs);
    }

    @Override
    public void compute(OpContext ctx) {
        DType out = Compute.promote(ctx.input(inputs().get(0)).dtype(),
                ctx.input(inputs().get(1)).dtype());
        typed(out).compute(ctx);
    }

    private OpTensor typed(DType out) {
        return switch (out) {
            case INT32 -> new IntMultiTensor(inputs(), outputs());
            case INT64 -> new LongMultiTensor(inputs(), outputs());
            case FLOAT32 -> new FloatMultiTensor(inputs(), outputs());
            default -> new DoubleMultiTensor(inputs(), outputs());
        };
    }
}
