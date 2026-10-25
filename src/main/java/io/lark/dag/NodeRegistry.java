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
 * A registry of business node factories keyed by a stable type name.
 *
 * <p>Java has no static-initialisation macro equivalent to {@code LARK_NODE},
 * so nodes are registered explicitly during application startup:
 *
 * <pre>{@code
 * NodeRegistry.instance().register("fetch_user", FetchUserNode::new);
 * }</pre>
 */
public final class NodeRegistry {

    private static final NodeRegistry INSTANCE = new NodeRegistry();

    private final ConcurrentMap<String, Supplier<? extends Node>> factories =
            new ConcurrentHashMap<>();

    /** Process-wide registry. Tests may create isolated instances instead. */
    public static NodeRegistry instance() {
        return INSTANCE;
    }

    /**
     * Register a factory under {@code typeName}.
     *
     * @throws IllegalArgumentException on a duplicate name
     */
    public void register(String typeName, Supplier<? extends Node> factory) {
        Objects.requireNonNull(typeName, "typeName");
        Objects.requireNonNull(factory, "factory");
        if (factories.putIfAbsent(typeName, factory) != null) {
            throw new IllegalArgumentException(
                    "NodeRegistry: duplicate node type '" + typeName + "'");
        }
    }

    public boolean contains(String typeName) {
        return factories.containsKey(typeName);
    }

    /**
     * Create a fresh node of {@code typeName}, tagged with {@code id}
     * (defaults to the type name).
     *
     * @throws NoSuchElementException when the type is unknown
     */
    public Node create(String typeName, String id) {
        Supplier<? extends Node> factory = factories.get(typeName);
        if (factory == null) {
            throw new NoSuchElementException(
                    "NodeRegistry: unknown node type '" + typeName + "'");
        }
        Node node = Objects.requireNonNull(factory.get(), "node factory returned null");
        node.setIdentity(id == null || id.isEmpty() ? typeName : id, typeName);
        return node;
    }

    public List<String> registeredTypes() {
        return factories.keySet().stream().sorted().toList();
    }
}
