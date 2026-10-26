// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.example.search;

import io.lark.column.biz.ColumnModule;
import io.lark.examples.search.SearchRankingOptions;
import io.lark.examples.search.SearchRankingService;
import io.lark.examples.search.SearchResult;
import io.lark.examples.search.data.SearchContext;
import io.lark.examples.search.data.SearchRepository;
import io.lark.examples.search.data.SearchRequest;
import io.lark.examples.search.module.AdjustedScoreModule;
import io.lark.examples.search.module.DelayModule;
import io.lark.examples.search.module.RetargetAdModule;
import io.lark.examples.search.module.SearchInputModule;
import io.lark.examples.search.module.TopKModule;
import io.lark.spring.ColumnPipelineFactory;
import io.lark.spring.EnableLaryColumns;
import io.lark.spring.LaryColumnAutoConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringSearchDemoTest {

    @Test
    void discoversAllSearchModulesAndRunsTypedSearch() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SearchConfig.class)) {
            assertEquals(13,
                    context.getBeanProvider(ColumnModule.class).stream().count());

            SearchContext searchContext = SearchRepository.sampleContext();
            SearchRequest request = SearchRepository.simulate(searchContext, 1_000);
            SearchRankingService service =
                    context.getBean(SearchRankingService.class);
            SearchResult result = service.search(request);

            assertEquals(30, result.ranked().size());
            assertEquals(1, result.ranked().get(0).rank());
            for (int i = 1; i < result.ranked().size(); i++) {
                assertTrue(result.ranked().get(i - 1).adjustedScore()
                        >= result.ranked().get(i).adjustedScore());
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableLaryColumns(basePackages = "io.lark.examples.search.module")
    @Import(LaryColumnAutoConfiguration.class)
    static class SearchConfig {

        @Bean
        SearchRankingOptions searchRankingOptions() {
            return new SearchRankingOptions(30);
        }

        @Bean
        SearchRankingService searchRankingService(
                ColumnPipelineFactory pipelines,
                SearchInputModule inputs,
                AdjustedScoreModule scores,
                TopKModule topK,
                RetargetAdModule retarget,
                DelayModule delay) {
            return new SearchRankingService(inputs, scores, topK, retarget, delay,
                    pipelines::create);
        }
    }
}
