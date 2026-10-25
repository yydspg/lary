// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.Objects;
import java.util.Optional;

/**
 * A named data stream flowing between business nodes.
 *
 * <p>A stream with a producer is a node output; a stream without a producer is
 * an external module input (fed from row data). Dependencies are expressed by
 * passing the stream object to another node's constructor — never by node
 * names.
 */
public final class ColumnStream {

    private final String name;
    private final ColumnNode producer;

    ColumnStream(String name, ColumnNode producer) {
        this.name = Objects.requireNonNull(name, "name");
        this.producer = producer;
    }

    public String name() {
        return name;
    }

    public Optional<ColumnNode> producer() {
        return Optional.ofNullable(producer);
    }

    public boolean isExternalFeed() {
        return producer == null;
    }

    @Override
    public String toString() {
        return "ColumnStream{" + name + (producer == null
                ? ", external" : ", producer=" + producer.id()) + '}';
    }
}
