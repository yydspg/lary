// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring;

import io.lark.spring.fixture.DoubledModule;
import io.lark.spring.fixture.NumberModule;
import io.lark.spring.scan.PropertyScanConfig;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LaryColumnPropertyScanTest {

    @Test
    void scanPackagesPropertyRegistersModules() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(
                    new MapPropertySource("test", Map.of(
                            "lary.column.scan-packages", "io.lark.spring.fixture")));
            context.register(PropertyScanConfig.class);
            context.refresh();

            assertNotNull(context.getBean(NumberModule.class));
            assertNotNull(context.getBean(DoubledModule.class));
            assertNotNull(context.getBean(ColumnPipelineFactory.class));
        }
    }
}
