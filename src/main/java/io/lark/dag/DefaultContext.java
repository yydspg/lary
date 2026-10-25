// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Default {@link IContext} implementation.
 *
 * <p>Keyed data lives in a {@link ConcurrentHashMap} (the map structure is
 * safe; value-level write conflicts remain the business layer's
 * responsibility, exactly as documented by {@link IContext}). Domain objects
 * are stored one per type.
 */
public class DefaultContext implements IContext {

    private record Entry(Object value) {
    }

    private final Map<String, Entry> data = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> domains = new ConcurrentHashMap<>();
    private final Map<PoolKind, ExecutorService> pools = new EnumMap<>(PoolKind.class);

    @Override
    public void setVoid(String key, Object value, Class<?> type) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "DefaultContext.set: value of type " + value.getClass().getName()
                            + " is not assignable to " + type.getName());
        }
        data.put(key, new Entry(value));
    }

    @Override
    public Object getVoid(String key, Class<?> type) {
        Entry entry = data.get(key);
        return entry != null && type.isInstance(entry.value()) ? entry.value() : null;
    }

    @Override
    public boolean has(String key, Class<?> type) {
        Entry entry = data.get(key);
        return entry != null && type.isInstance(entry.value());
    }

    @Override
    public void erase(String key) {
        data.remove(key);
    }

    @Override
    public void setDomain(Class<?> type, Object value) {
        domains.put(Objects.requireNonNull(type, "type"), Objects.requireNonNull(value, "value"));
    }

    @Override
    public Object getDomain(Class<?> type) {
        return domains.get(type);
    }

    @Override
    public ExecutorService pool(PoolKind kind) {
        ExecutorService pool = pools.get(kind);
        if (pool == null) {
            throw new IllegalStateException(
                    "DefaultContext: no pool installed for " + kind);
        }
        return pool;
    }

    @Override
    public void installPool(PoolKind kind, ExecutorService pool) {
        pools.put(Objects.requireNonNull(kind, "kind"), Objects.requireNonNull(pool, "pool"));
    }

    /** Number of keyed entries currently stored. */
    public int size() {
        return data.size();
    }

    /** Remove all keyed data and domain objects (pools are left installed). */
    public void clear() {
        data.clear();
        domains.clear();
    }
}
