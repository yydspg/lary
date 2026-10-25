// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Business graph of {@link ColumnNode}s.
 *
 * <p>Dependencies are normally derived from column names: if node B reads a
 * column that node A writes, A runs before B. {@link #dependsOn} adds an
 * explicit ordering edge when no data flow implies it.
 */
public final class ColumnGraph {

    /** Compiled order plus resolved dependency edges. */
    public record Compiled(
            List<ColumnNode> order,
            Map<ColumnNode, List<ColumnNode>> dependencies
    ) {
        public Compiled {
            order = List.copyOf(order);
            dependencies = Collections.unmodifiableMap(new IdentityHashMap<>(dependencies));
        }

        public List<ColumnNode> dependenciesOf(ColumnNode node) {
            return dependencies.getOrDefault(node, List.of());
        }
    }

    private record Edge(String node, String dependency) {
    }

    private final List<ColumnNode> nodes = new ArrayList<>();
    private final Map<String, ColumnNode> byName = new LinkedHashMap<>();
    private final List<Edge> explicitEdges = new ArrayList<>();

    public ColumnGraph add(ColumnNode node) {
        Objects.requireNonNull(node, "node");
        if (node.name() == null || node.name().isEmpty()) {
            throw new IllegalArgumentException("ColumnNode name must not be empty");
        }
        if (byName.putIfAbsent(node.name(), node) != null) {
            throw new IllegalArgumentException(
                    "ColumnGraph: duplicate node name '" + node.name() + "'");
        }
        nodes.add(node);
        return this;
    }

    public ColumnGraph addAll(ColumnNode... nodes) {
        for (ColumnNode node : nodes) {
            add(node);
        }
        return this;
    }

    /** Add an explicit ordering edge: {@code dependency} runs before {@code node}. */
    public ColumnGraph dependsOn(String node, String dependency) {
        explicitEdges.add(new Edge(
                Objects.requireNonNull(node, "node"),
                Objects.requireNonNull(dependency, "dependency")));
        return this;
    }

    public List<ColumnNode> nodes() {
        return List.copyOf(nodes);
    }

    public int size() {
        return nodes.size();
    }

    /**
     * Validate and topologically sort the business graph.
     *
     * @throws IllegalArgumentException on duplicate column producers, unknown
     *     explicit dependencies or self-dependencies
     * @throws IllegalStateException on a dependency cycle
     */
    public Compiled compile() {
        Map<ColumnNode, Set<ColumnNode>> deps = new IdentityHashMap<>();
        for (ColumnNode node : nodes) {
            deps.put(node, new LinkedHashSet<>());
        }

        Map<String, ColumnNode> producers = new LinkedHashMap<>();
        for (ColumnNode node : nodes) {
            for (String output : node.outputs()) {
                if (output == null || output.isEmpty()) {
                    continue;
                }
                ColumnNode previous = producers.putIfAbsent(output, node);
                if (previous != null && previous != node) {
                    throw new IllegalArgumentException(
                            "ColumnGraph: column '" + output + "' produced by both '"
                                    + previous.name() + "' and '" + node.name() + "'");
                }
            }
        }

        for (ColumnNode node : nodes) {
            for (String input : node.inputs()) {
                ColumnNode producer = producers.get(input);
                if (producer == null) {
                    continue; // feed column
                }
                if (producer == node) {
                    throw new IllegalArgumentException(
                            "ColumnGraph: node '" + node.name()
                                    + "' depends on its own column '" + input + "'");
                }
                deps.get(node).add(producer);
            }
        }

        for (Edge edge : explicitEdges) {
            ColumnNode node = byName.get(edge.node());
            ColumnNode dependency = byName.get(edge.dependency());
            if (node == null) {
                throw new IllegalArgumentException(
                        "ColumnGraph: unknown node '" + edge.node() + "'");
            }
            if (dependency == null) {
                throw new IllegalArgumentException(
                        "ColumnGraph: unknown dependency '" + edge.dependency() + "'");
            }
            if (node == dependency) {
                throw new IllegalArgumentException(
                        "ColumnGraph: node '" + node.name() + "' depends on itself");
            }
            deps.get(node).add(dependency);
        }

        Map<ColumnNode, Integer> inDegree = new IdentityHashMap<>();
        Map<ColumnNode, List<ColumnNode>> outgoing = new IdentityHashMap<>();
        for (ColumnNode node : nodes) {
            inDegree.put(node, deps.get(node).size());
            for (ColumnNode dependency : deps.get(node)) {
                outgoing.computeIfAbsent(dependency, key -> new ArrayList<>()).add(node);
            }
        }

        Deque<ColumnNode> ready = new ArrayDeque<>();
        for (ColumnNode node : nodes) {
            if (inDegree.get(node) == 0) {
                ready.add(node);
            }
        }

        List<ColumnNode> order = new ArrayList<>(nodes.size());
        while (!ready.isEmpty()) {
            ColumnNode node = ready.poll();
            order.add(node);
            for (ColumnNode next : outgoing.getOrDefault(node, List.of())) {
                int degree = inDegree.merge(next, -1, Integer::sum);
                if (degree == 0) {
                    ready.add(next);
                }
            }
        }

        if (order.size() != nodes.size()) {
            throw new IllegalStateException("ColumnGraph: dependency cycle detected");
        }

        Map<ColumnNode, List<ColumnNode>> resolved = new IdentityHashMap<>();
        for (ColumnNode node : nodes) {
            resolved.put(node, List.copyOf(deps.get(node)));
        }
        return new Compiled(order, resolved);
    }
}
