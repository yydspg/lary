// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

import java.util.List;

/** Division dispatcher; all division results are float64. */
public final class DivTensor extends OpTensor {

    private final OpTensor typed = new DoubleDivTensor(inputs(), outputs());

    public DivTensor(List<String> inputs, List<String> outputs) {
        super("div", inputs, outputs);
    }
    public DivTensor(String[] inputs, String[] outputs) {
        super("div", inputs, outputs);
    }

    @Override
    public void compute(OpContext ctx) {
        typed.compute(ctx);
    }
}
