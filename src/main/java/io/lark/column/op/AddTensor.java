// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import io.lark.column.DType;
import io.lark.column.compute.Compute;

import java.util.List;

/** Runtime dispatcher for add; chooses a typed AddTensor. */
public final class AddTensor extends OpTensor {

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
        typed(out).compute(ctx);
    }

    private OpTensor typed(DType out) {
        return switch (out) {
            case INT32 -> new IntAddTensor(inputs(), outputs());
            case INT64 -> new LongAddTensor(inputs(), outputs());
            case FLOAT32 -> new FloatAddTensor(inputs(), outputs());
            default -> new DoubleAddTensor(inputs(), outputs());
        };
    }
}
