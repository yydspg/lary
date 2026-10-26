// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.tensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Execution-layer intermediate storage: a name → tensor map without a uniform
 * row-count invariant. Filter / reduce ops may produce different lengths.
 *
 * <p>Parallel graph execution pre-registers every output name via
 * {@link #ensureNames(List)}; the map structure is thread-safe.
 */
public final class TensorStore {

    private final Map<String, Tensor> tensors = new ConcurrentHashMap<>();
    private final List<String> order = new ArrayList<>();
    private final Object orderLock = new Object();

    public void set(String name, Tensor tensor) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(tensor, "tensor");
        // Fast path: execution pre-registers every output name, so most writes
        // only need one concurrent map operation and no global lock.
        if (tensors.replace(name, tensor) != null) {
            return;
        }
        synchronized (orderLock) {
            Tensor previous = tensors.putIfAbsent(name, tensor);
            if (previous == null) {
                order.add(name);
            } else {
                tensors.put(name, tensor);
            }
        }
    }

    public void add(String name, Tensor tensor) {
        set(name, tensor);
    }

    public void ensureNames(List<String> names) {
        ensureNames(names.toArray(String[]::new));
    }

    public void ensureNames(String[] names) {
        synchronized (orderLock) {
            for (String name : names) {
                if (name != null && !name.isEmpty() && !tensors.containsKey(name)) {
                    order.add(name);
                    tensors.put(name, Tensor.zeros(0, DType.FLOAT64));
                }
            }
        }
    }

    public Tensor get(String name) {
        Tensor tensor = tensors.get(name);
        if (tensor == null) {
            throw new NoSuchElementException("TensorStore: unknown tensor '" + name + "'");
        }
        return tensor;
    }

    public Optional<Tensor> find(String name) {
        return Optional.ofNullable(tensors.get(name));
    }

    public boolean has(String name) {
        return tensors.containsKey(name);
    }

    public void erase(String name) {
        synchronized (orderLock) {
            if (tensors.remove(name) != null) {
                order.remove(name);
            }
        }
    }

    public void clear() {
        synchronized (orderLock) {
            tensors.clear();
            order.clear();
        }
    }

    public List<String> names() {
        synchronized (orderLock) {
            return List.copyOf(order);
        }
    }

    public int size() {
        return tensors.size();
    }

    public void addTable(TensorTable table) {
        addTable(table, true);
    }

    /**
     * Copy a table into the store.
     *
     * <p>{@code copy == false} stores the tensor references directly. Use it
     * only when the caller transfers ownership (the pipeline feed path does);
     * the caller must not retain or mutate those tensors afterwards.
     */
    public void addTable(TensorTable table, boolean copy) {
        for (String name : table.names()) {
            Tensor tensor = table.get(name);
            set(name, copy ? tensor.clone() : tensor);
        }
    }

    public TensorTable toTable(List<String> names) {
        return toTable(names, true);
    }

    /**
     * Materialize selected columns as a table.
     *
     * <p>{@code copy == false} returns a view over the stored tensors. The
     * caller must treat them as read-only; the pipeline fetch path uses it to
     * avoid copying every result column.
     */
    public TensorTable toTable(List<String> names, boolean copy) {
        TensorTable out = new TensorTable();
        Integer rows = null;
        for (String name : names) {
            Tensor tensor = tensors.get(name);
            if (tensor == null) {
                throw new NoSuchElementException(
                        "TensorStore.toTable: unknown tensor '" + name + "'");
            }
            if (rows == null) {
                rows = tensor.size();
            } else if (tensor.size() != rows) {
                throw new IllegalArgumentException(
                        "TensorStore.toTable: length mismatch for column '" + name + "'");
            }
            out.add(name, copy ? tensor.clone() : tensor);
        }
        return out;
    }

    public TensorStore clone() {
        TensorStore result = new TensorStore();
        for (String name : names()) {
            result.set(name, tensors.get(name).clone());
        }
        return result;
    }
}
