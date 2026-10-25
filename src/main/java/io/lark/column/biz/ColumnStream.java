// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.DType;

import java.util.Objects;
import java.util.Optional;

/**
 * A typed, named data stream flowing between business nodes.
 *
 * <p>Business code should not create {@code ColumnStream} anonymously or pass
 * string names around. Define a small column class instead:
 *
 * <pre>{@code
 * public final class CouponRoi extends DoubleColumn {
 * }
 * }</pre>
 *
 * <p>The column name is derived from the class name ({@code CouponRoi} →
 * {@code coupon_roi}); use the explicit-name constructor in the typed base
 * class when an external column has a different physical name. The generic
 * value type and the typed base class document the logical type;
 * {@link ColumnContext} validates the tensor dtype on read/write.
 *
 * <p>A stream with a producer is a node output; a stream without a producer is
 * an external module input (fed from row data). Dependencies are expressed by
 * passing the typed stream object to another node's constructor — never by
 * node names or positional indexes.
 */
public abstract class ColumnStream<T> {

    private final String explicitName;
    private final DType dtype;
    private volatile String resolvedName;
    private ColumnNode producer;

    /** Derive the stream name from the concrete column class. */
    protected ColumnStream(DType dtype) {
        this.explicitName = null;
        this.dtype = Objects.requireNonNull(dtype, "dtype");
    }

    /** Use an explicit physical name (for example a database column). */
    protected ColumnStream(String name, DType dtype) {
        this.explicitName = Objects.requireNonNull(name, "name");
        this.dtype = Objects.requireNonNull(dtype, "dtype");
    }

    public final String name() {
        if (explicitName != null) {
            return explicitName;
        }
        String name = resolvedName;
        if (name == null) {
            name = ColumnNames.of(getClass());
            resolvedName = name;
        }
        return name;
    }

    public final DType dtype() {
        return dtype;
    }

    public final Optional<ColumnNode> producer() {
        return Optional.ofNullable(producer);
    }

    public final boolean isExternalFeed() {
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
        return getClass().getSimpleName() + "{" + name()
                + (producer == null ? ", external" : ", producer=" + producer.id())
                + '}';
    }
}
