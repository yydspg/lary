// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Retargeting metrics, represented as a 2-D tensor per row set. */
@ColumnComponent
public final class RetargetAdModule extends ColumnModule {

    private ColumnStream<Double> retarget;

    @Override
    protected void define() {
        retarget = node(SearchOps.retargetAd(),
                feed(SearchMetrics.RETARGET_CLICKS),
                feed(SearchMetrics.CLICKS),
                feed(SearchMetrics.RETARGET_COST_FEN),
                feed(SearchMetrics.GMV_FEN));
        output(retarget);
    }

    public ColumnStream<Double> retargetAd() {
        return retarget;
    }
}
