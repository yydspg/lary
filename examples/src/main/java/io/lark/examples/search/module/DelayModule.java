// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.DelayNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Delayed-order/GMV signal, represented as a 3-D tensor. */
public final class DelayModule extends ColumnModule {

    private final DelayNode delay = node(new DelayNode(
            feed(SearchMetrics.DELAYED_ORDERS),
            feed(SearchMetrics.ORDERS),
            feed(SearchMetrics.DELAYED_GMV_FEN),
            feed(SearchMetrics.GMV_FEN)));

    public DelayModule() {
        output(delay.out());
    }

    public ColumnStream<Double> delay() {
        return delay.out();
    }
}
