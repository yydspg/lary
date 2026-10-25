// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.components;

import io.lark.column.Tensor;
import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.compute.Compute;

import java.util.List;

/** Test/demo component discovered by {@link io.lark.column.biz.ColumnComponentScanner}. */
@ColumnComponent
public final class ComponentSourceNode extends ColumnNode {

    @Override
    public String name() {
        return "component_source";
    }

    @Override
    public List<String> inputs() {
        return List.of("user_id");
    }

    @Override
    public List<String> outputs() {
        return List.of("component_source");
    }

    @Override
    public void execute(ColumnContext ctx) {
        Tensor userId = ctx.input("user_id");
        ctx.output("component_source", Compute.addScalar(userId, 1.0));
    }
}
