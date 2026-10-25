// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.compute.Compute;

import java.util.List;

/** Runtime dispatcher for subtraction; chooses a typed SubTensor. */
public final class SubTensor extends OpTensor {

    public SubTensor(List<String> inputs, List<String> outputs) {
        super("sub", inputs, outputs);
    }

    @Override
    public void compute(OpContext ctx) {
        DType out = Compute.promote(ctx.input(inputs().get(0)).dtype(),
                ctx.input(inputs().get(1)).dtype());
        typed(out).compute(ctx);
    }

    private OpTensor typed(DType out) {
        return switch (out) {
            case INT32 -> new IntSubTensor(inputs(), outputs());
            case INT64 -> new LongSubTensor(inputs(), outputs());
            case FLOAT32 -> new FloatSubTensor(inputs(), outputs());
            default -> new DoubleSubTensor(inputs(), outputs());
        };
    }
}
