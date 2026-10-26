// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.context.ExperimentContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;

class ExperimentPipelinesTest {

    @Test
    void routesToFreshPipelineForEachGraphVariant() {
        ExperimentPipelines pipelines = ExperimentPipelines
                .builder("rerank", ColumnPipeline::new)
                .variant("graph_boost", ColumnPipeline::new)
                .build();

        ExperimentContext control = ExperimentContext.builder()
                .variant("rerank", "control")
                .build();
        ExperimentContext boost = ExperimentContext.builder()
                .variant("rerank", "graph_boost")
                .build();

        try (ColumnPipeline first = pipelines.create(control);
             ColumnPipeline second = pipelines.create(boost)) {
            assertNotSame(first, second);
        }
    }
}
