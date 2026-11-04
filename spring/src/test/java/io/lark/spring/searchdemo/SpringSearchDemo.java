// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.searchdemo;

import io.lark.column.biz.ColumnPipeline;
import io.lark.examples.search.SearchDemo;
import io.lark.examples.search.SearchModule;
import io.lark.examples.search.SearchResult;
import io.lark.spring.ColumnPipelineFactory;
import io.lark.spring.EnableLaryColumns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The search demo running as a Spring Boot application.
 *
 * <p>{@link SearchModule} is the only {@code @ColumnComponent}; Spring discovers
 * it and {@link ColumnPipelineFactory} creates a fresh pipeline per request.
 *
 * <p>Run with {@code ./gradlew :spring:runSpringSearch}.
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableLaryColumns(basePackages = "io.lark.examples.search")
public class SpringSearchDemo {

    public static void main(String[] args) {
        SearchDemo.SearchContext context = SearchDemo.sampleContext();
        SearchDemo.SearchRequest request =
                SearchDemo.simulate(context, 1_000);
        try (ConfigurableApplicationContext spring =
                     SpringApplication.run(SpringSearchDemo.class, args)) {
            SearchModule module = spring.getBean(SearchModule.class);
            ColumnPipelineFactory pipelines =
                    spring.getBean(ColumnPipelineFactory.class);
            try (ColumnPipeline pipeline = pipelines.create()) {
                SearchResult result = module.search(pipeline, request);
                SearchDemo.print(result);
            }
        }
    }
}
