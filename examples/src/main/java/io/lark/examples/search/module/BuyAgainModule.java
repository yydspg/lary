// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Repeat-purchase share. */
public final class BuyAgainModule extends ColumnModule {

    private ColumnStream<Double> buyAgain;

    @Override
    protected void define() {
        buyAgain = node(SearchOps.ratio(SearchMetrics.BUYAGAIN),
                feed(SearchMetrics.BUY_AGAIN_ORDERS), feed(SearchMetrics.ORDERS));
        output(buyAgain);
    }

    public ColumnStream<Double> buyagain() {
        return buyAgain;
    }
}
