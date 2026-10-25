// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.fixture;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;

/** Feed module with a no-argument constructor. */
@ColumnComponent
public final class NumberModule extends ColumnModule {

    private final ColumnStream<Long> left = feed(NumberMetrics.LEFT);
    private final ColumnStream<Long> right = feed(NumberMetrics.RIGHT);
    private final SumNode sum = node(new SumNode(), left, right);

    public NumberModule() {
        fetch(left, right, sum.out());
    }

    public ColumnStream<Long> left() {
        return left;
    }

    public ColumnStream<Long> right() {
        return right;
    }

    public ColumnStream<Long> sum() {
        return sum.out();
    }
}
