// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.compute.Compute;

import java.util.List;

/** Runtime dispatcher for subtraction; chooses a typed SubTensor. */
public final class SubTensor extends OpTensor {

    private final OpTensor[] typed = {
            new IntSubTensor(inputs(), outputs()),
            new LongSubTensor(inputs(), outputs()),
            new FloatSubTensor(inputs(), outputs()),
            new DoubleSubTensor(inputs(), outputs())
    };

    public SubTensor(List<String> inputs, List<String> outputs) {
        super("sub", inputs, outputs);
    }
    public SubTensor(String[] inputs, String[] outputs) {
        super("sub", inputs, outputs);
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
