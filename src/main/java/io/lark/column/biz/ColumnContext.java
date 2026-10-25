// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;
import io.lark.column.TensorStore;

import java.util.Objects;

/** Business-facing stream read/write view. */
public final class ColumnContext {

    private final TensorStore store;

    public ColumnContext(TensorStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public Tensor read(ColumnStream stream) {
        return store.get(stream.name());
    }

    public boolean has(ColumnStream stream) {
        return store.has(stream.name());
    }

    public void write(ColumnStream stream, Tensor tensor) {
        store.set(stream.name(), tensor);
    }

    /** Advanced escape hatch for custom tensor plumbing. */
    public TensorStore store() {
        return store;
    }
}
