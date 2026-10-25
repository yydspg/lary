// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lightweight Spring-style component marker for {@link ColumnModule}
 * implementations.
 *
 * <p>Annotated modules are discovered by {@link ColumnComponentScanner} and can
 * be added with {@link ColumnPipeline#scan(String...)}. The class must have a
 * no-argument constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ColumnComponent {
}
