// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.Tensor;
import io.lark.column.tensor.TensorStore;

import java.util.Objects;

/**
 * Business-facing typed stream read/write view.
 *
 * <p>Use the column fields on the node directly:
 *
 * <pre>{@code
 * ctx.write(amountYuan, Compute.divScalar(ctx.read(amountFen), 100.0));
 * }</pre>
 *
 * <p>The declared {@link ColumnStream#dtype()} is checked on both read and
 * write, so a schema mistake fails at the business boundary instead of
 * silently propagating a wrongly typed column.
 */
public final class ColumnContext {

    private final TensorStore store;

    public ColumnContext(TensorStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public Tensor read(ColumnStream<?> stream) {
        Objects.requireNonNull(stream, "stream");
        Tensor tensor = store.get(stream.name());
        checkDtype(stream, tensor);
        return tensor;
    }

    public boolean has(ColumnStream<?> stream) {
        Objects.requireNonNull(stream, "stream");
        return store.has(stream.name());
    }

    public void write(ColumnStream<?> stream, Tensor tensor) {
        Objects.requireNonNull(stream, "stream");
        Objects.requireNonNull(tensor, "tensor");
        checkDtype(stream, tensor);
        store.set(stream.name(), tensor);
    }

    /** Advanced escape hatch for custom tensor plumbing. */
    public TensorStore store() {
        return store;
    }

    private static void checkDtype(ColumnStream<?> stream, Tensor tensor) {
        if (tensor.dtype() != stream.dtype()) {
            throw new IllegalArgumentException(
                    "Column '" + stream.name() + "' is declared as "
                            + stream.dtype() + " (" + stream.getClass().getSimpleName()
                            + ") but got " + tensor.dtype());
        }
    }
}
