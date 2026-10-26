// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.fixture;

import io.lark.column.tensor.Tensor;
import io.lark.column.biz.ColumnContext;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.NodeSchema;
import io.lark.column.biz.UnaryNode;
import io.lark.column.compute.Compute;

/** Doubles a long column without changing its dtype. */
public final class DoubleNode
        extends UnaryNode<ColumnStream<Long>, Long> {

    public static final NodeSchema<Long> SCHEMA =
            NodeSchema.of(NumberMetrics.DOUBLED);

    @Override
    protected Tensor compute(ColumnContext ctx, Tensor input) {
        return Compute.add(input, input);
    }
}
