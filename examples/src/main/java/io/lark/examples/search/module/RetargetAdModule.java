// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.RetargetAdNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Retargeting metrics, represented as a 2-D tensor per row set. */
public final class RetargetAdModule extends ColumnModule {

    private final RetargetAdNode retarget = node(new RetargetAdNode(
            feed(SearchMetrics.RETARGET_CLICKS),
            feed(SearchMetrics.CLICKS),
            feed(SearchMetrics.RETARGET_COST_FEN),
            feed(SearchMetrics.GMV_FEN)));

    public RetargetAdModule() {
        output(retarget.out());
    }

    public ColumnStream<Double> retargetAd() {
        return retarget.out();
    }
}
