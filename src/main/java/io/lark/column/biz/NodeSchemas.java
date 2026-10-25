// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Reads the static {@code SCHEMA} property declared by a node class.
 *
 * <p>Typed node bases use this so the constructor only takes input streams:
 *
 * <pre>{@code
 * public static final NodeSchema<Double> SCHEMA = NodeSchema.of(Metrics.CTR);
 *
 * public CtrNode(ColumnStream<Long> clicks, ColumnStream<Long> impressions) {
 *     super(clicks, impressions);
 * }
 * }</pre>
 */
final class NodeSchemas {

    private static final String FIELD = "SCHEMA";

    private static final ClassValue<NodeSchema<?>> CACHE = new ClassValue<>() {
        @Override
        protected NodeSchema<?> computeValue(Class<?> type) {
            try {
                Field field = type.getField(FIELD);
                if (!Modifier.isStatic(field.getModifiers())
                        || !NodeSchema.class.isAssignableFrom(field.getType())) {
                    throw new IllegalStateException(type.getName()
                            + ": " + FIELD + " must be a public static NodeSchema");
                }
                Object value = field.get(null);
                if (!(value instanceof NodeSchema<?> schema)) {
                    throw new IllegalStateException(type.getName()
                            + ": " + FIELD + " must be a NodeSchema");
                }
                return schema;
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(type.getName()
                        + " must declare public static final NodeSchema<> "
                        + FIELD, e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Cannot read " + FIELD + " from " + type.getName(), e);
            }
        }
    };

    private NodeSchemas() {
    }

    @SuppressWarnings("unchecked")
    static <O> NodeSchema<O> schemaOf(Class<?> type) {
        return (NodeSchema<O>) CACHE.get(type);
    }
}
