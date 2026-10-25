// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz.components;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnNode;
import io.lark.column.biz.UnitConvertNode;

import java.util.List;

/** Test/demo component: component_source (fen) → component_score (li). */
@ColumnComponent
public final class ComponentScaleNode extends ColumnNode {

    private final UnitConvertNode delegate =
            new UnitConvertNode("component_scale", "component_source",
                    "component_score", 10.0);

    @Override
    public String name() {
        return "component_scale";
    }

    @Override
    public List<String> inputs() {
        return List.of("component_source");
    }

    @Override
    public List<String> outputs() {
        return List.of("component_score");
    }

    @Override
    public void execute(ColumnContext ctx) {
        delegate.execute(ctx);
    }
}
