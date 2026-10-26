// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.fixture;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;

/** Feed module with a no-argument constructor. */
@ColumnComponent
public final class NumberModule extends ColumnModule {

    private ColumnStream<Long> left;
    private ColumnStream<Long> right;
    private SumNode sum;

    @Override
    protected void define() {
        left = feed(NumberMetrics.LEFT);
        right = feed(NumberMetrics.RIGHT);
        sum = node(new SumNode(), left, right);
        output(left, right, sum.out());
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
