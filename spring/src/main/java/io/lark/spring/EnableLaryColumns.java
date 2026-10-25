// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring;

import io.lark.column.biz.ColumnComponent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Enables classpath discovery of {@link ColumnComponent}-annotated modules and
 * registers them as Spring beans.
 *
 * <p>Modules may use constructor injection for other module beans, so a Spring
 * project does not need the no-argument constructor required by the
 * dependency-free {@link io.lark.column.biz.ColumnComponentScanner}.
 *
 * <pre>{@code
 * @SpringBootApplication
 * @EnableLaryColumns(basePackages = "com.example.features")
 * class Application {
 * }
 * }</pre>
 *
 * <p>If no packages are given, the package of the annotated class is used.
 * Packages from {@code lary.column.scan-packages} are always added, so
 * {@code @EnableLaryColumns} without arguments plus a property or
 * {@code application.yml} works too.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ColumnComponentRegistrar.class)
public @interface EnableLaryColumns {

    /** Packages to scan for {@link ColumnComponent} classes. */
    String[] basePackages() default {};

    /** Type-safe alternative to {@link #basePackages()}. */
    Class<?>[] basePackageClasses() default {};
}
