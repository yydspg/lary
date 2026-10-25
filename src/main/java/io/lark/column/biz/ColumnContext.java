// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.Tensor;
import io.lark.column.TensorStore;

import java.util.Objects;

/**
 * Business-facing view of the column store.
 *
 * <p>Business nodes read input columns and write output columns through this
 * context. They never see execution-layer types such as {@code OpTensor}.
 */
public final class ColumnContext {

    private final TensorStore store;

    public ColumnContext(TensorStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    /** Read an input column; throws when it is absent. */
    public Tensor input(String name) {
        return store.get(name);
    }

    public boolean has(String name) {
        return store.has(name);
    }

    /** Write/replace an output column. */
    public void output(String name, Tensor tensor) {
        store.set(name, tensor);
    }

    /** Advanced escape hatch for custom tensor plumbing. */
    public TensorStore store() {
        return store;
    }
}
