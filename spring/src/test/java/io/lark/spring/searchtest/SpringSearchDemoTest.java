// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.searchtest;

import io.lark.examples.search.model.SearchContext;
import io.lark.examples.search.model.SearchRequest;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;
import io.lark.examples.search.SearchDemo;
import io.lark.examples.search.module.SearchModule;
import io.lark.examples.search.result.SearchResult;
import io.lark.spring.ColumnPipelineFactory;
import io.lark.spring.EnableLaryColumns;
import io.lark.spring.LaryColumnAutoConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringSearchDemoTest {

    @Test
    void discoversTheSearchModuleAndRunsTypedSearch() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(SearchConfig.class)) {
            assertEquals(1,
                    context.getBeanProvider(ColumnModule.class).stream().count());

            SearchContext searchContext = SearchDemo.sampleContext();
            SearchRequest request =
                    SearchDemo.simulate(searchContext, 1_000);
            SearchModule module = context.getBean(SearchModule.class);
            ColumnPipelineFactory pipelines =
                    context.getBean(ColumnPipelineFactory.class);

            SearchResult result;
            try (ColumnPipeline pipeline = pipelines.create()) {
                result = module.search(pipeline, request);
            }

            assertEquals(30, result.ranked().size());
            assertEquals(1, result.ranked().get(0).rank());
            for (int i = 1; i < result.ranked().size(); i++) {
                assertTrue(result.ranked().get(i - 1).adjustedScore()
                        >= result.ranked().get(i).adjustedScore());
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableLaryColumns(basePackages = "io.lark.examples.search")
    @Import(LaryColumnAutoConfiguration.class)
    static class SearchConfig {
    }
}
