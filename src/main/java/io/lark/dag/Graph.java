// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.ArrayList;
import java.util.IdentityHashMap;
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

    private static final Node[] NO_NODES = new Node[0];

    private final List<Node> nodes;
    private final Node[] nodeArray;
    private final Map<String, Node> byId;
    private final Map<Node, Node[]> dependents;

    Graph(List<Node> nodes, Map<String, Node> byId) {
        this.nodes = List.copyOf(nodes);
        this.nodeArray = this.nodes.toArray(Node[]::new);
        this.byId = Map.copyOf(byId);

        Map<Node, List<Node>> builder = new IdentityHashMap<>();
        for (Node node : this.nodes) {
            for (Node dependency : node.dependencyArray()) {
                builder.computeIfAbsent(dependency, key -> new ArrayList<>())
                        .add(node);
            }
        }
        Map<Node, Node[]> adjacency = new IdentityHashMap<>();
        for (Map.Entry<Node, List<Node>> entry : builder.entrySet()) {
            adjacency.put(entry.getKey(), entry.getValue().toArray(Node[]::new));
        }
        this.dependents = adjacency;
    }

    public List<Node> nodes() {
        return nodes;
    }

    /**
     * Fixed array view of {@link #nodes()} for hot scheduler paths. The array
     * is owned by the graph and must not be modified.
     */
    public Node[] nodeArray() {
        return nodeArray;
    }

    /** Direct dependents of {@code node}, in node insertion order. */
    Node[] dependentsOf(Node node) {
        return dependents.getOrDefault(node, NO_NODES);
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
