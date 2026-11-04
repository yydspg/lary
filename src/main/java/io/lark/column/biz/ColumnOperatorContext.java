// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.context.ColumnRunContext;
import io.lark.column.context.ExperimentContext;

import java.util.Objects;

/**
 * Per-run view handed to a {@link ColumnOperator}.
 *
 * <p>Simple operators ignore it; contextual operators (for example one that
 * reads a request-scoped weight list) use {@link #runContext()}. The graph
 * itself stays context-free and reusable.
 */
public record ColumnOperatorContext(ExperimentContext experiment,
                                    ColumnRunContext runContext) {

    public ColumnOperatorContext {
        experiment = Objects.requireNonNull(experiment, "experiment");
        runContext = Objects.requireNonNull(runContext, "runContext");
    }
}
