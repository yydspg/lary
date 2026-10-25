// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Component marker for {@link ColumnModule} implementations.
 *
 * <p>{@link ColumnComponentScanner} and {@link ColumnPipeline#scan(String...)}
 * discover annotated modules without any Spring dependency; that scanner
 * requires a no-argument constructor. The optional {@code :spring} module
 * scans the same annotation with Spring and resolves constructor dependencies
 * through the container.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ColumnComponent {
}
