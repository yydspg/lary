// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Conversion rate. */
@ColumnComponent
public final class CvrModule extends ColumnModule {

    private ColumnStream<Long> clicks;
    private ColumnStream<Long> orders;
    private ColumnStream<Double> cvr;

    @Override
    protected void define() {
        clicks = feed(SearchMetrics.CLICKS);
        orders = feed(SearchMetrics.ORDERS);
        cvr = node(SearchOps.ratio(SearchMetrics.CVR), orders, clicks);
        output(cvr);
    }

    public ColumnStream<Double> cvr() {
        return cvr;
    }
}
