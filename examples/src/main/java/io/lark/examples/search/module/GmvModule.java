// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Gross merchandise value in yuan. */
@ColumnComponent
public final class GmvModule extends ColumnModule {

    private ColumnStream<Double> gmv;

    @Override
    protected void define() {
        gmv = node(SearchOps.scale(SearchMetrics.GMV, 100.0),
                feed(SearchMetrics.GMV_FEN));
        output(gmv);
    }

    public ColumnStream<Double> gmv() {
        return gmv;
    }
}
