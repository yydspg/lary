// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.ColumnDefinitionException;
import io.lark.column.tensor.DType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-pipeline registry of node outputs.
 *
 * <p>Named outputs are keyed by column name; anonymous nodes are keyed by
 * operator + input column names + dtype. A second module can therefore reuse a
 * common node by calling {@link ColumnModule#shared(Column)} or by issuing the
 * same {@code node(...)} definition.
 */
final class ColumnNodeRegistry {

    record NodeKey(String operator, DType dtype, List<String> inputs) {
    }

    private final Map<String, ColumnStream<?>> byOutput = new LinkedHashMap<>();
    private final Map<NodeKey, ColumnStream<?>> byDefinition = new LinkedHashMap<>();
    private final Map<String, List<String>> dependencies = new LinkedHashMap<>();

    /** Copy already-built nodes into a new pipeline registry. */
    void adopt(ColumnNodeRegistry other) {
        if (other == this) {
            return;
        }
        other.byOutput.forEach(byOutput::putIfAbsent);
        other.byDefinition.forEach(byDefinition::putIfAbsent);
        other.dependencies.forEach(dependencies::putIfAbsent);
    }

    ColumnStream<?> output(String name) {
        return byOutput.get(name);
    }

    void registerOutput(ColumnStream<?> stream) {
        ColumnStream<?> existing = byOutput.putIfAbsent(
                stream.name(), stream);
        if (existing != null && existing != stream) {
            throw new ColumnDefinitionException(
                    "ColumnNodeRegistry: duplicate output '" + stream.name()
                            + "'");
        }
        ColumnNode producer = stream.producer().orElse(null);
        if (producer != null) {
            List<String> inputs = new ArrayList<>();
            for (ColumnStream<?> feed : producer.feedView()) {
                inputs.add(feed.name());
            }
            dependencies.put(stream.name(), List.copyOf(inputs));
        }
    }

    /** Reject cycles between shared/named node outputs. */
    void validateAcyclic() {
        Map<String, Integer> state = new LinkedHashMap<>();
        Deque<String> path = new ArrayDeque<>();
        for (String name : byOutput.keySet()) {
            visit(name, state, path);
        }
    }

    private void visit(String name, Map<String, Integer> state,
                       Deque<String> path) {
        Integer current = state.get(name);
        if (current != null && current == 2) {
            return;
        }
        if (current != null && current == 1) {
            throw new ColumnDefinitionException(
                    "ColumnNodeRegistry: shared node cycle at '" + name
                            + "' via " + path);
        }
        state.put(name, 1);
        path.addLast(name);
        for (String input : dependencies.getOrDefault(name, List.of())) {
            if (byOutput.containsKey(input)) {
                visit(input, state, path);
            }
        }
        path.removeLast();
        state.put(name, 2);
    }

    ColumnStream<?> definition(NodeKey key) {
        return byDefinition.get(key);
    }

    void registerDefinition(NodeKey key, ColumnStream<?> stream) {
        byDefinition.putIfAbsent(key, stream);
    }

    static NodeKey key(String operator, DType dtype,
                       ColumnStream<?>[] inputs) {
        String[] names = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            names[i] = inputs[i].name();
        }
        return new NodeKey(operator, dtype, List.of(names));
    }
}
