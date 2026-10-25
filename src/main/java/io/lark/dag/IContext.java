// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * Abstract per-request, type-erased data bag shared by all nodes / ops.
 *
 * <p>Port of {@code lark::IContext}. The framework only depends on this
 * interface, so business code can plug in a custom storage strategy (tracing,
 * request-scoped services, test doubles, ...).
 *
 * <p><strong>Concurrency contract.</strong> The framework orchestrates nodes
 * asynchronously. A write performed by one node is visible to any node that
 * later awaits its completion (the executor establishes the happens-before
 * edge). Business code MUST ensure that no two concurrently running nodes
 * write the same key; concurrent reads, and concurrent writes to distinct
 * keys, are safe.
 *
 * <p>Values are stored together with their runtime type. Use
 * {@link #set(String, Object, Class)} when the declared type matters (for
 * example a {@code List} stored as an {@code ArrayList}).
 */
public interface IContext {

    /** Keyed write. {@code type} is the declared type used by {@link #get}. */
    void setVoid(String key, Object value, Class<?> type);

    /** Keyed read; returns {@code null} when missing or of a different type. */
    Object getVoid(String key, Class<?> type);

    /**
     * Whether {@code key} holds a value compatible with {@code type}.
     */
    boolean has(String key, Class<?> type);

    /** Remove a keyed value. */
    void erase(String key);

    // ---- domain contexts -------------------------------------------------

    /** Store a request-scoped domain object under its type. */
    void setDomain(Class<?> type, Object value);

    /** Retrieve a domain object; {@code null} when absent. */
    Object getDomain(Class<?> type);

    // ---- worker pools ----------------------------------------------------

    /**
     * Worker pool of the given class. The pools are owned by the executor and
     * valid for the duration of the current execution.
     */
    ExecutorService pool(PoolKind kind);

    /**
     * Called by the executor before a run. Custom contexts may ignore this;
     * {@link DefaultContext} records the executor-owned pools.
     */
    default void installPool(PoolKind kind, ExecutorService pool) {
        // Optional for custom implementations.
    }

    // ---- typed convenience ----------------------------------------------

    /** Write a value, deriving its declared type from {@code value.getClass()}. */
    default <T> void set(String key, T value) {
        Objects.requireNonNull(value, "value");
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) value.getClass();
        set(key, value, type);
    }

    /** Write a value with an explicit declared type. */
    default <T> void set(String key, T value, Class<? super T> type) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        setVoid(key, value, type);
    }

    /**
     * Read a value of {@code type}.
     *
     * @throws NoSuchElementException when the key is missing or holds a
     *     different type
     */
    default <T> T get(String key, Class<T> type) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");
        Object value = getVoid(key, type);
        if (value == null) {
            throw new NoSuchElementException(
                    "IContext.get: missing key '" + key + "' of type " + type.getName());
        }
        return type.cast(value);
    }

    /** Alias of {@link #get} that fails when the key is absent. */
    default <T> T require(String key, Class<T> type) {
        return get(key, type);
    }

    /** Store {@code value} as a domain object under its runtime type. */
    default <T> T provideDomain(T value) {
        Objects.requireNonNull(value, "value");
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) value.getClass();
        setDomain(type, value);
        return value;
    }

    /** Create and store a domain object under {@code type}. */
    default <T> T provideDomain(Class<T> type, Supplier<? extends T> supplier) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(supplier, "supplier");
        T value = Objects.requireNonNull(supplier.get(), "domain supplier returned null");
        setDomain(type, value);
        return value;
    }

    /**
     * Retrieve a domain object.
     *
     * @throws NoSuchElementException when no object is registered for the type
     */
    default <T> T domain(Class<T> type) {
        Objects.requireNonNull(type, "type");
        Object value = getDomain(type);
        if (value == null) {
            throw new NoSuchElementException(
                    "IContext.domain: missing '" + type.getName() + "'");
        }
        return type.cast(value);
    }

    /** Alias of {@link #domain} that fails when absent. */
    default <T> T requireDomain(Class<T> type) {
        return domain(type);
    }
}
