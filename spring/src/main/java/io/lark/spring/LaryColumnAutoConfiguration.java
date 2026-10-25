// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * Spring Boot auto-configuration for lary column pipelines.
 *
 * <p>All {@link ColumnModule} beans are collected into a
 * {@link ColumnPipelineFactory}. {@link EnableLaryColumns} (or ordinary
 * {@code @Bean}/{@code @Component} definitions) can be used to create those
 * beans. A prototype {@link ColumnPipeline} bean is also available; prefer the
 * factory because a pipeline is mutable and should be used for one run.
 */
@AutoConfiguration
@ConditionalOnClass(ColumnPipeline.class)
@ConditionalOnProperty(prefix = "lary.column", name = "enabled",
        havingValue = "true", matchIfMissing = true)
@Import(ColumnComponentRegistrar.class)
public class LaryColumnAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ColumnPipelineFactory columnPipelineFactory(
            ObjectProvider<ColumnModule> modules,
            @Value("${lary.column.compute-workers:0}") int computeWorkers) {
        return new ColumnPipelineFactory(
                modules.orderedStream().toList(), computeWorkers);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean
    public ColumnPipeline columnPipeline(ColumnPipelineFactory factory) {
        return factory.create();
    }
}
