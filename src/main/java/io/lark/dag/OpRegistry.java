// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Registry of op factories keyed by a stable type name.
 *
 * <p>Java has no static-initialisation macro equivalent to {@code LARK_OP},
 * so ops are registered explicitly during application startup:
 *
 * <pre>{@code
 * OpRegistry.instance().register("DataFetchOp", DataFetchOp::new);
 * }</pre>
 */
public final class OpRegistry {

    private static final OpRegistry INSTANCE = new OpRegistry();

    private final ConcurrentMap<String, Supplier<? extends Op>> factories =
            new ConcurrentHashMap<>();

    public static OpRegistry instance() {
        return INSTANCE;
    }

    /**
     * Register a factory under {@code typeName}.
     *
     * @throws IllegalArgumentException on a duplicate name
     */
    public void register(String typeName, Supplier<? extends Op> factory) {
        Objects.requireNonNull(typeName, "typeName");
        Objects.requireNonNull(factory, "factory");
        if (factories.putIfAbsent(typeName, factory) != null) {
            throw new IllegalArgumentException(
                    "OpRegistry: duplicate op type '" + typeName + "'");
        }
    }

    public boolean contains(String typeName) {
        return factories.containsKey(typeName);
    }

    /**
     * Create a fresh op of {@code typeName}. The optional {@code id} is
     * informational (the op carries its own business name).
     *
     * @throws NoSuchElementException when the type is unknown
     */
    public Op create(String typeName, String id) {
        Supplier<? extends Op> factory = factories.get(typeName);
        if (factory == null) {
            throw new NoSuchElementException(
                    "OpRegistry: unknown op type '" + typeName + "'");
        }
        return Objects.requireNonNull(factory.get(), "op factory returned null");
    }

    public Op create(String typeName) {
        return create(typeName, "");
    }

    public List<String> registeredTypes() {
        return factories.keySet().stream().sorted().toList();
    }
}
