// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Click-through rate. */
@ColumnComponent
public final class CtrModule extends ColumnModule {

    private ColumnStream<Long> impressions;
    private ColumnStream<Long> clicks;
    private ColumnStream<Double> ctr;

    @Override
    protected void define() {
        impressions = feed(SearchMetrics.IMPRESSIONS);
        clicks = feed(SearchMetrics.CLICKS);
        ctr = node(SearchOps.ratio(SearchMetrics.CTR), clicks, impressions);
        output(ctr);
    }

    public ColumnStream<Double> ctr() {
        return ctr;
    }
}
