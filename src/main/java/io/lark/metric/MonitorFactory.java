// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Register / select monitor implementations by name.
 *
 * <p>Registered by default: "null", "logging", "stats", "composite".
 */
public final class MonitorFactory {

    private static final MonitorFactory INSTANCE = new MonitorFactory();

    private final ConcurrentMap<String, Supplier<? extends Monitor>> factories =
            new ConcurrentHashMap<>();

    private MonitorFactory() {
        register("null", () -> NullMonitor.INSTANCE);
        register("logging", LoggingMonitor::new);
        register("stats", StatsMonitor::new);
        register("composite", CompositeMonitor::new);
    }

    public static MonitorFactory instance() {
        return INSTANCE;
    }

    public void register(String name, Supplier<? extends Monitor> factory) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(factory, "factory");
        if (factories.putIfAbsent(name, factory) != null) {
            throw new IllegalArgumentException("MonitorFactory: duplicate monitor '" + name + "'");
        }
    }

    public Monitor create(String name) {
        Supplier<? extends Monitor> factory = factories.get(name);
        if (factory == null) {
            throw new IllegalArgumentException("MonitorFactory: unknown monitor '" + name + "'");
        }
        return Objects.requireNonNull(factory.get(), "monitor factory returned null");
    }

    public boolean contains(String name) {
        return factories.containsKey(name);
    }

    public List<String> available() {
        return factories.keySet().stream().sorted().toList();
    }
}
