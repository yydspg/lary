// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.RatioNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Conversion rate. */
public final class CvrModule extends ColumnModule {

    private final ColumnStream<Long> clicks = feed(SearchMetrics.CLICKS);
    private final ColumnStream<Long> orders = feed(SearchMetrics.ORDERS);
    private final RatioNode cvr = node(new RatioNode(SearchMetrics.CVR),
            orders, clicks);

    public CvrModule() {
        output(cvr.out());
    }

    public ColumnStream<Double> cvr() {
        return cvr.out();
    }
}
