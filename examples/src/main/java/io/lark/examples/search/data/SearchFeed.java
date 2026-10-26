// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import io.lark.column.Row;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.examples.search.module.SearchInputModule;

import java.util.List;

/** Builds the typed search feed from the sample repository. */
public final class SearchFeed {

    private SearchFeed() {
    }

    public static FeedStream sample(SearchInputModule inputs) {
        return of(inputs, SearchRepository.rows());
    }

    public static FeedStream of(SearchInputModule inputs, List<Row> rows) {
        return FeedStream.of(new ColumnStream<?>[]{
                inputs.impressions(),
                inputs.clicks(),
                inputs.orders(),
                inputs.gmvFen(),
                inputs.couponCostFen(),
                inputs.buyAgainOrders(),
                inputs.delayedOrders(),
                inputs.delayedGmvFen(),
                inputs.retargetClicks(),
                inputs.retargetCostFen()
        }, rows);
    }
}
