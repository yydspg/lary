// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Per-run experiment assignments and parameters.
 *
 * <p>Search systems need to vary formulas by request/page-view without
 * rebuilding a graph. A pipeline can install an {@code ExperimentContext}
 * before {@code compute()}; formula nodes read it at evaluation time through
 * {@code F.variant(...)} and {@code F.param(...)}.
 */
public final class ExperimentContext {

    private static final ExperimentContext EMPTY =
            new ExperimentContext(Map.of(), Map.of());

    private final Map<String, String> variants;
    private final Map<String, Double> parameters;

    private ExperimentContext(Map<String, String> variants,
                              Map<String, Double> parameters) {
        this.variants = Collections.unmodifiableMap(
                new LinkedHashMap<>(variants));
        this.parameters = Collections.unmodifiableMap(
                new LinkedHashMap<>(parameters));
    }

    public static ExperimentContext empty() {
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Variant assigned to a layer, or {@code null} when the layer is absent. */
    public String variant(String layer) {
        return variants.get(Objects.requireNonNull(layer, "layer"));
    }

    public String variant(String layer, String fallback) {
        String assigned = variants.get(Objects.requireNonNull(layer, "layer"));
        return assigned == null ? fallback : assigned;
    }

    public boolean is(String layer, String variant) {
        return Objects.equals(variants.get(Objects.requireNonNull(layer, "layer")),
                Objects.requireNonNull(variant, "variant"));
    }

    public double param(String key, double fallback) {
        return parameters.getOrDefault(
                Objects.requireNonNull(key, "key"), fallback);
    }

    public Map<String, String> variants() {
        return variants;
    }

    public Map<String, Double> parameters() {
        return parameters;
    }

    public static final class Builder {

        private final Map<String, String> variants = new LinkedHashMap<>();
        private final Map<String, Double> parameters = new LinkedHashMap<>();

        public Builder variant(String layer, String variant) {
            variants.put(Objects.requireNonNull(layer, "layer"),
                    Objects.requireNonNull(variant, "variant"));
            return this;
        }

        public Builder param(String key, double value) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("experiment parameter must be finite");
            }
            parameters.put(Objects.requireNonNull(key, "key"), value);
            return this;
        }

        public ExperimentContext build() {
            return new ExperimentContext(variants, parameters);
        }
    }
}
