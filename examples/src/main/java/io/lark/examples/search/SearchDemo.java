// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search;

import io.lark.examples.search.data.SearchContext;
import io.lark.examples.search.data.SearchRepository;
import io.lark.examples.search.data.SearchRequest;

/**
 * Simulated search ranking demo.
 *
 * <p>A {@link SearchContext} is paired with a {@code GoodsList} of 1,000
 * candidate {@code GoodsDO} rows. {@link SearchRankingService} runs the graph
 * and returns a typed {@link SearchResult}: selected goods in score order plus
 * the retargeting and delay tensors.
 *
 * <p>Run with {@code ./gradlew :examples:runSearch}.
 */
public final class SearchDemo {

    private static final int CANDIDATES = 1_000;
    private static final int WORKERS = 4;

    private SearchDemo() {
    }

    public static void main(String[] args) {
        SearchContext context = SearchRepository.sampleContext();
        SearchRequest request = SearchRepository.simulate(context, CANDIDATES);

        SearchRankingService service = SearchRankingService.create(
                context.topK(), WORKERS);
        SearchResult result = service.search(request);
        SearchResultPrinter.print(result);
    }
}
