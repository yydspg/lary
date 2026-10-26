// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.data;

import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.column.data.Row;
import io.lark.examples.search.module.SearchInputModule;

import java.util.List;

/** Builds the typed search feed from a search request. */
public final class SearchFeed {

    private SearchFeed() {
    }

    public static FeedStream sample(SearchInputModule inputs) {
        return of(inputs, SearchRepository.sampleRequest());
    }

    public static FeedStream of(SearchInputModule inputs, SearchRequest request) {
        return of(inputs, SearchRepository.rows(request));
    }

    public static FeedStream of(SearchInputModule inputs, List<Row> rows) {
        return FeedStream.of(new ColumnStream<?>[]{
                inputs.pvId(),
                inputs.goodsId(),
                inputs.impressions(),
                inputs.clicks(),
                inputs.orders(),
                inputs.gmvFen(),
                inputs.couponCostFen(),
                inputs.buyAgainOrders(),
                inputs.delayedOrders(),
                inputs.delayedGmvFen(),
                inputs.retargetClicks(),
                inputs.retargetCostFen(),
                inputs.perturbation(),
                inputs.originalScore()
        }, rows);
    }
}
