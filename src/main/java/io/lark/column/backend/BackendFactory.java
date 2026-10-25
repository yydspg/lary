// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.backend;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/** Registry of compute backends; "cpu" is built in. */
public final class BackendFactory {

    private static final BackendFactory INSTANCE = new BackendFactory();

    private final ConcurrentMap<String, Supplier<? extends Backend>> factories =
            new ConcurrentHashMap<>();

    private BackendFactory() {
        factories.put("cpu", CpuBackend::new);
    }

    public static BackendFactory instance() {
        return INSTANCE;
    }

    public void register(String name, Supplier<? extends Backend> factory) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(factory, "factory");
        if (factories.putIfAbsent(name, factory) != null) {
            throw new IllegalArgumentException(
                    "BackendFactory: duplicate backend '" + name + "'");
        }
    }

    public Backend create(String name) {
        Supplier<? extends Backend> factory = factories.get(name);
        if (factory == null) {
            throw new NoSuchElementException(
                    "BackendFactory: unknown backend '" + name + "'");
        }
        return Objects.requireNonNull(factory.get(), "backend factory returned null");
    }

    public boolean contains(String name) {
        return factories.containsKey(name);
    }

    public List<String> available() {
        return factories.keySet().stream().sorted().toList();
    }

    public static Backend createCpuBackend() {
        return new CpuBackend();
    }
}
