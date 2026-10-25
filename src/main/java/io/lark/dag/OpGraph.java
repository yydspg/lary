// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A graph of business ops with dependency edges and aspects.
 *
 * <p>{@link #compile()} validates the graph and produces a topological order
 * (Kahn's algorithm); it is called automatically by {@link OpExecutor} and is
 * safe to call repeatedly.
 */
public final class OpGraph {

    private final List<Op> insertionOrder = new ArrayList<>();
    private final Map<String, Op> byId = new LinkedHashMap<>();
    private final Map<Op, List<Op>> dependencies = new IdentityHashMap<>();
    private final List<OpAspect> aspects = new ArrayList<>();

    private List<Op> topologicalOrder = List.of();
    private boolean compiled;

    /** Add an op under its {@link Op#name()}. */
    public OpGraph addOp(Op op) {
        return addOp(op, null);
    }

    /**
     * Add an op under a node id. {@code id} defaults to the op's name.
     * Multiple ops of the same type may be added under distinct ids.
     */
    public OpGraph addOp(Op op, String id) {
        Objects.requireNonNull(op, "op");
        String nodeId = id == null || id.isEmpty() ? op.name() : id;
        if (byId.putIfAbsent(nodeId, op) != null) {
            throw new IllegalArgumentException(
                    "OpGraph: duplicate node id '" + nodeId + "'");
        }
        insertionOrder.add(op);
        compiled = false;
        return this;
    }

    /** Attach a cross-cutting aspect applied around every op. */
    public OpGraph addAspect(OpAspect aspect) {
        if (aspect != null) {
            aspects.add(aspect);
        }
        return this;
    }

    /** {@code dependency} must finish before {@code dependent} runs. */
    public OpGraph dependsOn(String dependency, String dependent) {
        Op dep = byId.get(dependency);
        if (dep == null) {
            throw new IllegalArgumentException(
                    "OpGraph: unknown dependency '" + dependency + "'");
        }
        Op depnd = byId.get(dependent);
        if (depnd == null) {
            throw new IllegalArgumentException(
                    "OpGraph: unknown dependent '" + dependent + "'");
        }
        dependencies.computeIfAbsent(depnd, key -> new ArrayList<>()).add(dep);
        compiled = false;
        return this;
    }

    /**
     * Validate and produce a topological order.
     *
     * @throws IllegalStateException when the graph contains a cycle
     */
    public List<Op> compile() {
        if (compiled) {
            return topologicalOrder;
        }

        Map<Op, Integer> inDegree = new IdentityHashMap<>();
        Map<Op, List<Op>> outgoing = new IdentityHashMap<>();
        for (Op op : insertionOrder) {
            inDegree.put(op, 0);
        }
        for (Op op : insertionOrder) {
            for (Op dep : depsOf(op)) {
                outgoing.computeIfAbsent(dep, key -> new ArrayList<>()).add(op);
                inDegree.merge(op, 1, Integer::sum);
            }
        }

        List<Op> ready = new ArrayList<>();
        for (Op op : insertionOrder) {
            if (inDegree.get(op) == 0) {
                ready.add(op);
            }
        }

        List<Op> order = new ArrayList<>(insertionOrder.size());
        int cursor = 0;
        while (cursor < ready.size()) {
            Op op = ready.get(cursor++);
            order.add(op);
            for (Op next : outgoing.getOrDefault(op, List.of())) {
                int degree = inDegree.merge(next, -1, Integer::sum);
                if (degree == 0) {
                    ready.add(next);
                }
            }
        }

        if (order.size() != insertionOrder.size()) {
            throw new IllegalStateException("OpGraph: dependency cycle detected");
        }

        topologicalOrder = List.copyOf(order);
        compiled = true;
        return topologicalOrder;
    }

    /** Ops in topological order after {@link #compile()}, else insertion order. */
    public List<Op> ops() {
        return compiled ? topologicalOrder : List.copyOf(insertionOrder);
    }

    public List<Op> depsOf(Op op) {
        return Collections.unmodifiableList(
                dependencies.getOrDefault(op, List.of()));
    }

    public List<OpAspect> aspects() {
        return List.copyOf(aspects);
    }

    public int size() {
        return insertionOrder.size();
    }

    public boolean compiled() {
        return compiled;
    }
}
