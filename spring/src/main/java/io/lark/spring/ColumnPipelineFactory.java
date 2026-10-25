// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnPipeline;

import java.util.List;
import java.util.Objects;

/**
 * Creates a fresh {@link ColumnPipeline} for each run from the Spring-managed
 * {@link ColumnModule} beans.
 *
 * <p>A pipeline is mutable (it holds the compiled graph and the current feed
 * table), so it is intentionally not a singleton. Inject this factory and call
 * {@link #create()} inside a try-with-resources block:
 *
 * <pre>{@code
 * try (ColumnPipeline pipeline = pipelines.create()) {
 *     pipeline.compile();
 *     pipeline.feed(feed);
 *     pipeline.compute();
 *     return pipeline.fetch(fetch);
 * }
 * }</pre>
 */
public final class ColumnPipelineFactory {

    private final List<ColumnModule> modules;
    private final int computeWorkers;
    private final boolean virtualThreads;

    public ColumnPipelineFactory(List<ColumnModule> modules, int computeWorkers) {
        this(modules, computeWorkers, false);
    }

    public ColumnPipelineFactory(List<ColumnModule> modules, int computeWorkers,
                                 boolean virtualThreads) {
        this.modules = List.copyOf(Objects.requireNonNull(modules, "modules"));
        this.computeWorkers = computeWorkers;
        this.virtualThreads = virtualThreads;
    }

    /** Create an uncompiled pipeline containing the Spring-managed modules. */
    public ColumnPipeline create() {
        ColumnPipeline pipeline = computeWorkers > 0
                ? new ColumnPipeline(computeWorkers)
                : new ColumnPipeline();
        pipeline.setVirtualComputeThreads(virtualThreads);
        pipeline.addAll(modules);
        return pipeline;
    }

}
