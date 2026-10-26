// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.ScaleNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Gross merchandise value in yuan. */
public final class GmvModule extends ColumnModule {

    private final ScaleNode gmv = node(new ScaleNode(SearchMetrics.GMV, 100.0),
            feed(SearchMetrics.GMV_FEN));

    public GmvModule() {
        output(gmv.out());
    }

    public ColumnStream<Double> gmv() {
        return gmv.out();
    }
}
