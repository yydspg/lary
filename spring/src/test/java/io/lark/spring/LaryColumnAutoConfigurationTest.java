// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring;

import io.lark.column.data.Cell;
import io.lark.column.data.Row;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;
import io.lark.column.biz.ColumnStream;
import io.lark.column.biz.FeedStream;
import io.lark.column.biz.FetchStream;
import io.lark.spring.fixture.DoubledModule;
import io.lark.spring.fixture.NumberModule;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LaryColumnAutoConfigurationTest {

    @Test
    void scansModulesAndWiresConstructorDependencies() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AnnotationConfig.class)) {
            NumberModule numbers = context.getBean(NumberModule.class);
            DoubledModule doubled = context.getBean(DoubledModule.class);
            assertSame(numbers, context.getBean(NumberModule.class));

            ColumnPipelineFactory factory =
                    context.getBean(ColumnPipelineFactory.class);
            assertEquals(2L,
                    context.getBeanProvider(ColumnModule.class).stream().count());

            try (ColumnPipeline pipeline = factory.create()) {
                pipeline.compile(FetchStream.of(doubled.doubled()));
                pipeline.feed(FeedStream.of(
                        new ColumnStream<?>[]{numbers.left(), numbers.right()},
                        List.of(Row.of(Cell.of(2), Cell.of(3)),
                                Row.of(Cell.of(5), Cell.of(7)))));
                pipeline.compute();

                assertEquals(List.of(10L, 24L),
                        pipeline.fetch(FetchStream.of(doubled.doubled())).stream()
                                .map(row -> row.get(0).asLong())
                                .toList());
            }
        }
    }

    @Test
    void pipelineBeanIsPrototype() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AnnotationConfig.class)) {
            ColumnPipeline first = context.getBean(ColumnPipeline.class);
            ColumnPipeline second = context.getBean(ColumnPipeline.class);
            try {
                assertNotSame(first, second);
            } finally {
                first.close();
                second.close();
            }
        }
    }

    @Test
    void virtualThreadsPropertyConfiguresTheFactory() {
        String key = "lary.column.virtual-threads";
        String previous = System.getProperty(key);
        System.setProperty(key, "true");
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AnnotationConfig.class)) {
            try (ColumnPipeline pipeline =
                         context.getBean(ColumnPipelineFactory.class).create()) {
                assertTrue(pipeline.usesVirtualComputeThreads());
            }
        } finally {
            if (previous == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, previous);
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableLaryColumns(basePackages = "io.lark.spring.fixture")
    @Import(LaryColumnAutoConfiguration.class)
    static class AnnotationConfig {
    }
}
