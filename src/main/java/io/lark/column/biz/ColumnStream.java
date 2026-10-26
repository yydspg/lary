// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.data.DType;

import java.util.Objects;
import java.util.Optional;

/**
 * A typed stream handle bound to a {@link Column} definition.
 *
 * <p>Handles are created by the framework: a node output is created with
 * {@link ColumnNode#fetch(Column)} and an external input with
 * {@link ColumnModule#input(Column)}. Business code passes handles between
 * nodes and reads/writes them through {@link ColumnContext}; it does not create
 * handles or use string names.
 *
 * <p>A handle with a producer is a node output; a handle without a producer is
 * an external module input.
 */
public final class ColumnStream<T> {

    private final Column<T> column;
    private ColumnNode producer;

    ColumnStream(Column<T> column, ColumnNode producer) {
        this.column = Objects.requireNonNull(column, "column");
        this.producer = producer;
    }

    public Column<T> definition() {
        return column;
    }

    public String name() {
        return column.name();
    }

    public DType dtype() {
        return column.dtype();
    }

    public Optional<ColumnNode> producer() {
        return Optional.ofNullable(producer);
    }

    public boolean isExternalFeed() {
        return producer == null;
    }

    final void bindProducer(ColumnNode producer) {
        Objects.requireNonNull(producer, "producer");
        ColumnNode current = this.producer;
        if (current != null && current != producer) {
            throw new IllegalStateException(
                    "ColumnStream '" + name() + "' already belongs to "
                            + current.id());
        }
        this.producer = producer;
    }

    @Override
    public String toString() {
        return column + (producer == null ? " {external}" : " {" + producer.id() + '}');
    }
}
