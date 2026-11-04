// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.List;

/**
 * Immutable scheduler metadata for a compiled {@link Graph}.
 *
 * <p>A graph is structurally immutable after {@link GraphBuilder} returns, so
 * the dependency counters, reverse adjacency and root set only need to be
 * computed once. The executor copies the primitive indegree array per run and
 * reuses the adjacency arrays, avoiding an identity map and one array per node
 * on every execution.
 */
final class GraphPlan {

    private final Node[] nodes;
    private final int[] initialPending;
    private final int[][] dependentIndices;
    private final int[] roots;

    GraphPlan(List<Node> nodes) {
        int count = nodes.size();
        this.nodes = nodes.toArray(Node[]::new);
        this.initialPending = new int[count];
        this.dependentIndices = new int[count][];

        for (int i = 0; i < count; i++) {
            this.nodes[i].setGraphIndex(i);
            this.initialPending[i] = this.nodes[i].dependencyArray().length;
        }

        int[] outDegree = new int[count];
        for (int i = 0; i < count; i++) {
            for (Node dependency : this.nodes[i].dependencyArray()) {
                outDegree[indexOf(dependency)]++;
            }
        }
        for (int i = 0; i < count; i++) {
            dependentIndices[i] = new int[outDegree[i]];
        }
        int[] cursor = new int[count];
        for (int i = 0; i < count; i++) {
            for (Node dependency : this.nodes[i].dependencyArray()) {
                int dependencyIndex = indexOf(dependency);
                dependentIndices[dependencyIndex][cursor[dependencyIndex]++] = i;
            }
        }

        int rootCount = 0;
        for (int i = 0; i < count; i++) {
            if (initialPending[i] == 0) {
                rootCount++;
            }
        }
        this.roots = new int[rootCount];
        int root = 0;
        for (int i = 0; i < count; i++) {
            if (initialPending[i] == 0) {
                roots[root++] = i;
            }
        }
    }

    private int indexOf(Node node) {
        int value = node.graphIndex();
        if (value < 0 || value >= nodes.length) {
            throw new IllegalStateException(
                    "GraphPlan: dependency outside this graph: " + node.id());
        }
        return value;
    }

    Node[] nodes() {
        return nodes;
    }

    /** Fresh copy for the per-run dependency counters. */
    int[] initialPending() {
        return initialPending.clone();
    }

    /** Reverse adjacency: {@code dependentIndices[i]} lists nodes released by i. */
    int[][] dependentIndices() {
        return dependentIndices;
    }

    int[] roots() {
        return roots;
    }
}
