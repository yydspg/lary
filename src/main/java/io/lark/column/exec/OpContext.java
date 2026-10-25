// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import io.lark.column.Tensor;
import io.lark.column.TensorStore;

import java.util.Objects;

/** Read/write view handed to a {@link TensorOp} during compute. */
public final class OpContext {

    private final TensorStore store;

    public OpContext(TensorStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public Tensor input(String name) {
        return store.get(name);
    }

    public boolean has(String name) {
        return store.has(name);
    }

    public void set(String name, Tensor tensor) {
        store.set(name, tensor);
    }

    public TensorStore store() {
        return store;
    }
}
