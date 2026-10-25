// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.exec.ComputeGraph;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable output of {@link PipelineCompiler}: the execution graph and every
 * boundary/precondition derived from it.
 *
 * <p>Keeping the plan in one object means a recompile is an atomic swap. The
 * pipeline never observes a graph with feed/output metadata from another
 * compile.
 */
record PipelinePlan(
        ComputeGraph graph,
        Set<String> requiredFeedColumns,
        List<String> requiredFeedColumnOrder,
        Map<String, ColumnStream<?>> feedColumnsByName,
        List<String> outputs,
        Set<String> outputSet,
        int nodeCount) {

    PipelinePlan {
        Objects.requireNonNull(graph, "graph");
        requiredFeedColumns = Set.copyOf(requiredFeedColumns);
        requiredFeedColumnOrder = List.copyOf(requiredFeedColumnOrder);
        feedColumnsByName = Map.copyOf(feedColumnsByName);
        outputs = List.copyOf(outputs);
        outputSet = Set.copyOf(outputs);
        if (nodeCount < 0) {
            throw new IllegalArgumentException("nodeCount must be >= 0");
        }
    }

    boolean canFetch(List<String> names) {
        return outputSet.containsAll(names);
    }
}
