// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.fixture;

import io.lark.column.data.Tensor;
import io.lark.column.biz.BinaryNode;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.column.compute.Compute;

/** Adds two long columns. */
public final class SumNode
        extends BinaryNode<ColumnStream<Long>, ColumnStream<Long>, Long> {

    public static final NodeSchema<Long> SCHEMA =
            NodeSchema.of(NumberMetrics.SUM);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor left, Tensor right) {
        return Compute.add(left, right);
    }
}
