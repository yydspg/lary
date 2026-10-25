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
 * is the array of dependency ids that must complete before it runs.
 *
 * <pre>{@code
 * new NodeDef("fetch_user")                           // no deps, id == type
 * new NodeDef("aggregate", "fetch_user")              // deps by id
 * NodeDef.of("score", "score_v2", "aggregate")        // explicit id + deps
 * new NodeDef("score", List.of("aggregate"), "score_v2")
 * }</pre>
 *
 * <p>Dependencies are stored as an array because a definition's arity is
 * fixed; the {@link List} constructors are convenience overloads.
 */
public record NodeDef(String type, String[] deps, String id) {

    public NodeDef {
        Objects.requireNonNull(type, "type");
        deps = deps == null ? new String[0] : deps.clone();
        id = id == null ? "" : id;
    }

    public NodeDef(String type) {
        this(type, new String[0], "");
    }

    /** Dependencies as varargs; the id defaults to the type. */
    public NodeDef(String type, String... deps) {
        this(type, deps, "");
    }

    public NodeDef(String type, List<String> deps) {
        this(type, deps, "");
    }

    public NodeDef(String type, List<String> deps, String id) {
        this(type, deps == null ? new String[0]
                : deps.toArray(String[]::new), id);
    }

    /** Explicit id plus varargs dependencies. */
    public static NodeDef of(String type, String id, String... deps) {
        return new NodeDef(type, deps, id);
    }
}
