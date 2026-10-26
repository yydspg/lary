// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.example.search;

import io.lark.examples.search.SearchRankingOptions;
import io.lark.examples.search.SearchRankingService;
import io.lark.examples.search.SearchResult;
import io.lark.examples.search.SearchResultPrinter;
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * The search demo running as a Spring Boot application.
 *
 * <p>All search modules are {@code @ColumnComponent}s. Spring discovers them,
 * resolves their constructor dependencies, and {@link ColumnPipelineFactory}
 * creates the graph on demand. There is no manual module list in this class.
 *
 * <p>Only runtime concerns remain explicit: the request rows and the business
 * facade. {@link SearchRankingService} hides row/column plumbing.
 *
 * <p>Run with {@code ./gradlew :spring:runSpringSearch}.
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableLaryColumns(basePackages = "io.lark.examples.search.module")
public class SpringSearchDemo {

    @Bean
    public SearchRankingOptions searchRankingOptions() {
        return new SearchRankingOptions(30);
    }

    @Bean
    public SearchRankingService searchRankingService(
            ColumnPipelineFactory pipelines,
            SearchInputModule inputs,
            AdjustedScoreModule scores,
            TopKModule topK,
            RetargetAdModule retarget,
            DelayModule delay) {
        return new SearchRankingService(inputs, scores, topK, retarget, delay,
                pipelines::create);
    }

    public static void main(String[] args) {
        SearchContext context = SearchRepository.sampleContext();
        SearchRequest request = SearchRepository.simulate(context, 1_000);
        try (ConfigurableApplicationContext spring =
                     SpringApplication.run(SpringSearchDemo.class, args)) {
            SearchRankingService service =
                    spring.getBean(SearchRankingService.class);
            SearchResult result = service.search(request);
            SearchResultPrinter.print(result);
        }
    }
}
