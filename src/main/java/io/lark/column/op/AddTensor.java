// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.data.DType;
import io.lark.column.compute.Compute;

import java.util.List;

/** Runtime dispatcher for add; chooses a typed AddTensor. */
public final class AddTensor extends OpTensor {

    private final OpTensor[] typed = {
            new IntAddTensor(inputs(), outputs()),
            new LongAddTensor(inputs(), outputs()),
            new FloatAddTensor(inputs(), outputs()),
            new DoubleAddTensor(inputs(), outputs())
    };

    public AddTensor(List<String> inputs, List<String> outputs) {
        super("add", inputs, outputs);
    }
    public AddTensor(String[] inputs, String[] outputs) {
        super("add", inputs, outputs);
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
