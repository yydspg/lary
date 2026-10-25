// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.op.OpSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** The business definition of a module — an ordered list of ops. */
public final class SubGraph {

    private final List<OpSpec> ops = new ArrayList<>();

    public SubGraph op(OpSpec spec) {
        ops.add(Objects.requireNonNull(spec, "spec"));
        return this;
    }

    public SubGraph add(OpSpec spec) {
        return op(spec);
    }

    public List<OpSpec> ops() {
        return List.copyOf(ops);
    }

    public int size() {
        return ops.size();
    }

    public boolean isEmpty() {
        return ops.isEmpty();
    }

    public void clear() {
        ops.clear();
    }
}
