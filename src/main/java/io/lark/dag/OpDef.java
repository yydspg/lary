// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.List;
import java.util.Objects;

/**
 * KV-style op description: {@code id : registeredType}.
 *
 * <p>{@code id} defaults to {@code type} when empty.
 */
public record OpDef(String type, List<String> deps, String id) {

    public OpDef {
        Objects.requireNonNull(type, "type");
        deps = deps == null ? List.of() : List.copyOf(deps);
        id = id == null ? "" : id;
    }

    public OpDef(String type) {
        this(type, List.of(), "");
    }

    public OpDef(String type, List<String> deps) {
        this(type, deps, "");
    }
}
