// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.RatioNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Click-through rate. */
public final class CtrModule extends ColumnModule {

    private final ColumnStream<Long> impressions = feed(SearchMetrics.IMPRESSIONS);
    private final ColumnStream<Long> clicks = feed(SearchMetrics.CLICKS);
    private final RatioNode ctr = node(new RatioNode(SearchMetrics.CTR),
            clicks, impressions);

    public CtrModule() {
        output(ctr.out());
    }

    public ColumnStream<Double> ctr() {
        return ctr.out();
    }
}
