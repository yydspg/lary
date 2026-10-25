// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.List;
import java.util.Objects;

/**
 * One key/value entry describing a node in a graph specification.
 *
 * <p>The "key" is the node (its registered {@code type}, and an optional
 * distinct {@code id} when the same type appears multiple times); the "value"
 * is the list of dependency ids that must complete before it runs.
 *
 * <pre>{@code
 * new NodeDef("fetch_user")                           // no deps, id == type
 * new NodeDef("aggregate", List.of("fetch_user"))     // deps by id
 * new NodeDef("score", List.of("aggregate"), "score_v2")
 * }</pre>
 */
public record NodeDef(String type, List<String> deps, String id) {

    public NodeDef {
        Objects.requireNonNull(type, "type");
        deps = deps == null ? List.of() : List.copyOf(deps);
        id = id == null ? "" : id;
    }

    public NodeDef(String type) {
        this(type, List.of(), "");
    }

    public NodeDef(String type, List<String> deps) {
        this(type, deps, "");
    }
}
