// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Fans every event out to a set of child monitors. */
public final class CompositeMonitor implements Monitor {

    private final CopyOnWriteArrayList<Monitor> children = new CopyOnWriteArrayList<>();

    public CompositeMonitor add(Monitor child) {
        children.add(Objects.requireNonNull(child, "child"));
        return this;
    }

    public int size() {
        return children.size();
    }

    public List<Monitor> children() {
        return List.copyOf(children);
    }

    @Override
    public void emit(Event event) {
        for (Monitor child : children) {
            child.emit(event);
        }
    }
}
