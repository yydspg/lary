// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An immutable, validated directed acyclic graph of node instances.
 *
 * <p>Only {@link GraphBuilder} constructs a graph. Per-run state lives on the
 * nodes and is reset by the executor, so one graph may be executed repeatedly.
 */
public final class Graph {

    private final List<Node> nodes;
    private final Map<String, Node> byId;

    Graph(List<Node> nodes, Map<String, Node> byId) {
        this.nodes = List.copyOf(nodes);
        this.byId = Map.copyOf(byId);
    }

    public List<Node> nodes() {
        return nodes;
    }

    public int size() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /** Look up a node by its unique id. */
    public Optional<Node> find(String id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(byId.get(id));
    }

    /** Clear per-run state on all nodes so the graph can be executed again. */
    void resetRunState() {
        for (Node node : nodes) {
            node.resetRunState();
        }
    }
}
