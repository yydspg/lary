// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds and validates a {@link Graph} from an array of key/value
 * {@link NodeDef}s.
 *
 * <p>Validation is split into three deterministic passes, mirroring the C++
 * {@code GraphBuilder}: materialize/index, wire dependencies, reject cycles
 * (iterative DFS with three-colour marking).
 */
public final class GraphBuilder {

    private final NodeRegistry registry;

    /** Uses the process-wide registry. */
    public GraphBuilder() {
        this(NodeRegistry.instance());
    }

    /** Uses a custom registry (useful for tests / isolated graphs). */
    public GraphBuilder(NodeRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public Graph build(NodeDef... defs) {
        return build(List.of(defs));
    }

    /**
     * Materialize a graph.
     *
     * @throws IllegalArgumentException on an unknown node type, duplicate id,
     *     missing dependency id, self-dependency or cycle
     */
    public Graph build(List<NodeDef> defs) {
        Objects.requireNonNull(defs, "defs");

        List<Node> nodes = new ArrayList<>(defs.size());
        Map<String, Node> byId = new LinkedHashMap<>();
        createAndIndexNodes(defs, nodes, byId);
        wireDependencies(defs, nodes, byId);
        rejectCycles(nodes);
        return new Graph(nodes, byId);
    }

    // ---- pass 1: materialize node instances and index them by id ---------

    private void createAndIndexNodes(List<NodeDef> defs, List<Node> nodes,
                                     Map<String, Node> byId) {
        for (NodeDef def : defs) {
            if (def.type().isEmpty()) {
                throw new IllegalArgumentException("GraphBuilder: node def has empty type");
            }
            String id = def.id().isEmpty() ? def.type() : def.id();
            Node node;
            try {
                node = registry.create(def.type(), id);
            } catch (java.util.NoSuchElementException e) {
                throw new IllegalArgumentException(
                        "GraphBuilder: unknown node type '" + def.type() + "'", e);
            }
            if (byId.putIfAbsent(id, node) != null) {
                throw new IllegalArgumentException(
                        "GraphBuilder: duplicate node id '" + id + "'");
            }
            nodes.add(node);
        }
    }

    // ---- pass 2: resolve dependency ids and attach them ------------------

    private static void wireDependencies(List<NodeDef> defs, List<Node> nodes,
                                         Map<String, Node> byId) {
        for (int i = 0; i < defs.size(); i++) {
            Node node = nodes.get(i);
            for (String depId : defs.get(i).deps()) {
                Node dep = byId.get(depId);
                if (dep == null) {
                    throw new IllegalArgumentException(
                            "GraphBuilder: node '" + node.id()
                                    + "' depends on unknown id '" + depId + "'");
                }
                if (dep == node) {
                    throw new IllegalArgumentException(
                            "GraphBuilder: node '" + node.id() + "' depends on itself");
                }
                node.addDependency(dep);
            }
        }
    }

    // ---- pass 3: detect cycles with an iterative DFS ---------------------

    private enum Mark {
        UNVISITED, IN_STACK, DONE
    }

    private record Frame(Node node, int index) {
    }

    private static void rejectCycles(List<Node> nodes) {
        Map<Node, Mark> marks = new IdentityHashMap<>();

        for (Node start : nodes) {
            if (marks.getOrDefault(start, Mark.UNVISITED) != Mark.UNVISITED) {
                continue;
            }

            Deque<Frame> stack = new ArrayDeque<>();
            stack.push(new Frame(start, 0));
            marks.put(start, Mark.IN_STACK);

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                List<Node> dependencies = frame.node().dependencies();

                if (frame.index() < dependencies.size()) {
                    Node next = dependencies.get(frame.index());
                    stack.pop();
                    stack.push(new Frame(frame.node(), frame.index() + 1));

                    Mark mark = marks.getOrDefault(next, Mark.UNVISITED);
                    if (mark == Mark.IN_STACK) {
                        throw new IllegalArgumentException(
                                "GraphBuilder: dependency cycle detected involving node '"
                                        + next.id() + "'");
                    }
                    if (mark == Mark.UNVISITED) {
                        marks.put(next, Mark.IN_STACK);
                        stack.push(new Frame(next, 0));
                    }
                } else {
                    marks.put(frame.node(), Mark.DONE);
                    stack.pop();
                }
            }
        }
    }
}
