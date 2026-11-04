// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.context;

import io.lark.column.ColumnExecutionException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Typed business context for one pipeline run.
 *
 * <p>Graph topology is defined statically in
 * {@link io.lark.column.biz.ColumnModule#buildGraph()}. Request-scoped values
 * that are not already columns (features, weights, configuration) travel here,
 * not through graph definitions:
 *
 * <pre>{@code
 * pipeline.setRunContext(ColumnRunContext.of(searchContext));
 *
 * // inside a custom operator
 * SearchContext request = context.runContext().require(SearchContext.class);
 * }</pre>
 *
 * <p>Values are keyed by type. Use the {@link Builder} when a value should be
 * looked up by an interface or superclass. This context is per run and must not
 * be used to rebuild the graph.
 */
public final class ColumnRunContext {

    private static final ColumnRunContext EMPTY =
            new ColumnRunContext(Map.of());

    private final Map<Class<?>, Object> values;

    private ColumnRunContext(Map<Class<?>, Object> values) {
        this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    public static ColumnRunContext empty() {
        return EMPTY;
    }

    /** Context keyed by the runtime class of each value. */
    public static ColumnRunContext of(Object... values) {
        Objects.requireNonNull(values, "values");
        Builder builder = builder();
        for (Object value : values) {
            builder.put(value);
        }
        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean has(Class<?> type) {
        return values.containsKey(Objects.requireNonNull(type, "type"));
    }

    /** Typed value, or {@code null} when absent. */
    public <T> T get(Class<T> type) {
        return type.cast(values.get(Objects.requireNonNull(type, "type")));
    }

    public <T> Optional<T> find(Class<T> type) {
        return Optional.ofNullable(get(type));
    }

    /** Typed value; fails when the run context does not carry it. */
    public <T> T require(Class<T> type) {
        Objects.requireNonNull(type, "type");
        Object value = values.get(type);
        if (value == null) {
            throw new ColumnExecutionException(
                    "ColumnRunContext: missing value of type " + type.getName());
        }
        return type.cast(value);
    }

    /** Immutable type-to-value view. */
    public Map<Class<?>, Object> values() {
        return values;
    }

    public static final class Builder {

        private final Map<Class<?>, Object> values = new LinkedHashMap<>();

        /** Register a value under its runtime class. */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Builder put(Object value) {
            Objects.requireNonNull(value, "value");
            return put((Class) value.getClass(), value);
        }

        /** Register a value under an interface or superclass key. */
        public <T> Builder put(Class<T> type, T value) {
            Objects.requireNonNull(type, "type");
            values.put(type, Objects.requireNonNull(value, "value"));
            return this;
        }

        public ColumnRunContext build() {
            return new ColumnRunContext(values);
        }
    }
}
