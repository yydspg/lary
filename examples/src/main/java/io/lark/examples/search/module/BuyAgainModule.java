// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.RatioNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Repeat-purchase share. */
public final class BuyAgainModule extends ColumnModule {

    private final RatioNode buyAgain = node(new RatioNode(SearchMetrics.BUYAGAIN),
            feed(SearchMetrics.BUY_AGAIN_ORDERS), feed(SearchMetrics.ORDERS));

    public BuyAgainModule() {
        output(buyAgain.out());
    }

    public ColumnStream<Double> buyagain() {
        return buyAgain.out();
    }
}
