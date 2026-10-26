// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.fixture;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;

/** Module that needs constructor injection of another module. */
@ColumnComponent
public final class DoubledModule extends ColumnModule {

    private final NumberModule numbers;
    private DoubleNode doubled;

    public DoubledModule(NumberModule numbers) {
        this.numbers = numbers;
    }

    @Override
    protected void define() {
        doubled = node(new DoubleNode(), numbers.published(NumberMetrics.SUM));
        dependsOn(NumberModule.class);
        output(doubled.out());
    }

    public ColumnStream<Long> doubled() {
        return doubled.out();
    }
}
