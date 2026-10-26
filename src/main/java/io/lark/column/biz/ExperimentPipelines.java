// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.context.ExperimentContext;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Selects a pre-built pipeline graph for an experiment variant.
 *
 * <p>Formula-level variants use {@link F#chooseVariant}. When an experiment
 * needs a genuinely different graph topology, register one pipeline factory
 * per variant and call {@link #create(ExperimentContext)}. The framework still
 * keeps pipeline creation and lifecycle explicit; this object only routes the
 * request to the correct fresh pipeline.
 */
public final class ExperimentPipelines {

    private final String layer;
    private final Supplier<ColumnPipeline> fallback;
    private final Map<String, Supplier<ColumnPipeline>> variants;

    private ExperimentPipelines(String layer,
                                Supplier<ColumnPipeline> fallback,
                                Map<String, Supplier<ColumnPipeline>> variants) {
        this.layer = Objects.requireNonNull(layer, "layer");
        this.fallback = Objects.requireNonNull(fallback, "fallback");
        this.variants = Map.copyOf(variants);
    }

    public static Builder builder(String layer,
                                  Supplier<ColumnPipeline> fallback) {
        return new Builder(layer, fallback);
    }

    /** Create a fresh pipeline for the variant assigned to {@code layer}. */
    public ColumnPipeline create(ExperimentContext experiment) {
        Objects.requireNonNull(experiment, "experiment");
        String variant = experiment.variant(layer);
        Supplier<ColumnPipeline> supplier = variant == null
                ? fallback : variants.getOrDefault(variant, fallback);
        return Objects.requireNonNull(supplier.get(), "pipeline");
    }

    public String layer() {
        return layer;
    }

    public static final class Builder {

        private final String layer;
        private final Supplier<ColumnPipeline> fallback;
        private final Map<String, Supplier<ColumnPipeline>> variants =
                new LinkedHashMap<>();

        private Builder(String layer, Supplier<ColumnPipeline> fallback) {
            this.layer = Objects.requireNonNull(layer, "layer");
            this.fallback = Objects.requireNonNull(fallback, "fallback");
        }

        public Builder variant(String name, Supplier<ColumnPipeline> pipelines) {
            variants.put(Objects.requireNonNull(name, "name"),
                    Objects.requireNonNull(pipelines, "pipelines"));
            return this;
        }

        public ExperimentPipelines build() {
            return new ExperimentPipelines(layer, fallback, variants);
        }
    }
}
