// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/** A collection of named tensors (columns) with equal length. */
public final class TensorTable {

    private final Map<String, Tensor> tensors = new LinkedHashMap<>();
    private final List<String> order = new ArrayList<>();
    private int numRows;

    public void add(String name, Tensor tensor) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(tensor, "tensor");
        if (!tensors.isEmpty() && tensor.size() != numRows) {
            throw new IllegalArgumentException(
                    "Tensor size mismatch: expected " + numRows
                            + ", got " + tensor.size());
        }
        if (!tensors.containsKey(name)) {
            order.add(name);
        }
        tensors.put(name, tensor);
        numRows = tensor.size();
    }

    public void set(String name, Tensor tensor) {
        add(name, tensor);
    }

    public Tensor get(String name) {
        Tensor tensor = tensors.get(name);
        if (tensor == null) {
            throw new NoSuchElementException("Tensor not found: " + name);
        }
        return tensor;
    }

    public boolean has(String name) {
        return tensors.containsKey(name);
    }

    public void remove(String name) {
        if (tensors.remove(name) == null) {
            return;
        }
        order.remove(name);
        if (tensors.isEmpty()) {
            numRows = 0;
        }
    }

    public int numRows() {
        return numRows;
    }

    public int numCols() {
        return tensors.size();
    }

    public boolean isEmpty() {
        return tensors.isEmpty();
    }

    public List<String> names() {
        return List.copyOf(order);
    }

    public TensorTable clone() {
        TensorTable result = new TensorTable();
        for (String name : order) {
            result.add(name, tensors.get(name).clone());
        }
        return result;
    }
}
