// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Serializable description of a computation op — the bridge between the
 * business layer (DSL / module code) and the execution layer.
 */
public record OpSpec(String type, List<String> inputs, List<String> outputs,
                     Map<String, String> attrs) {

    public OpSpec {
        Objects.requireNonNull(type, "type");
        inputs = inputs == null ? List.of() : List.copyOf(inputs);
        outputs = outputs == null ? List.of() : List.copyOf(outputs);
        attrs = attrs == null ? Map.of() : Map.copyOf(attrs);
    }

    public OpSpec(String type, List<String> inputs, List<String> outputs) {
        this(type, inputs, outputs, Map.of());
    }

    public boolean hasAttr(String key) {
        return attrs.containsKey(key);
    }

    public String attr(String key) {
        return attr(key, "");
    }

    public String attr(String key, String fallback) {
        return attrs.getOrDefault(key, fallback);
    }
}
