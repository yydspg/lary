// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/** Instantiates typed column classes declared by business schema code. */
final class ColumnStreams {

    private ColumnStreams() {
    }

    static <T extends ColumnStream<?>> T create(Class<T> type) {
        Objects.requireNonNull(type, "column type");
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null) && !constructor.trySetAccessible()) {
                throw new IllegalArgumentException(
                        "Column class " + type.getName()
                                + " must have an accessible no-arg constructor");
            }
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Column class " + type.getName()
                            + " must declare a no-arg constructor", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "Cannot create column class " + type.getName(), e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() == null ? e : e.getCause();
            throw new IllegalArgumentException(
                    "Column class " + type.getName() + " constructor failed", cause);
        }
    }
}
