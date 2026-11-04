// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.context;

import java.util.Objects;

/**
 * Framework compile-time context for { build}.
 *
 * <p>Business code can subclass it to carry its own typed configuration, or
 * use the generic value bag:
 *
 * <pre>{@code
 * final class SearchGraphContext extends ColumnGraphContext {
 *     private final int topK;
 *     SearchGraphContext(ExperimentContext experiment, int topK) {
 *         super(experiment, ColumnRunContext.empty());
 *         this.topK = topK;
 *     }
 *     int topK() { return topK; }
 * }
 *
 * @Override protected void build(ColumnGraphContext context) {
 *     SearchGraphContext search = context.as(SearchGraphContext.class);
 *     ...
 * }
 * }</pre>
 *
 * <p>This context is for graph construction only. Per-request values belong to
 * {@link ColumnRunContext}; per-request formula branches use
 * {@link ExperimentContext} at execution time.
 */
public class ColumnGraphContext {

    private static final ColumnGraphContext EMPTY =
            new ColumnGraphContext(ExperimentContext.empty(),
                    ColumnRunContext.empty());

    private final ExperimentContext experiment;
    private final ColumnRunContext values;

    protected ColumnGraphContext(ExperimentContext experiment,
                                 ColumnRunContext values) {
        this.experiment = Objects.requireNonNull(experiment, "experiment");
        this.values = Objects.requireNonNull(values, "values");
    }

    public static ColumnGraphContext empty() {
        return EMPTY;
    }

    public static ColumnGraphContext of(ExperimentContext experiment) {
        return new ColumnGraphContext(experiment, ColumnRunContext.empty());
    }

    public static ColumnGraphContext of(ExperimentContext experiment,
                                        Object... values) {
        return new ColumnGraphContext(experiment, ColumnRunContext.of(values));
    }

    public static ColumnGraphContext of(ExperimentContext experiment,
                                        ColumnRunContext values) {
        return new ColumnGraphContext(experiment, values);
    }

    public ExperimentContext experiment() {
        return experiment;
    }

    public ColumnRunContext values() {
        return values;
    }

    public <T> T get(Class<T> type) {
        return values.get(type);
    }

    public <T> T require(Class<T> type) {
        return values.require(type);
    }

    /** Cast this context to a business subclass. */
    public <C extends ColumnGraphContext> C as(Class<C> type) {
        return type.cast(this);
    }
}
