// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * Registers {@link ColumnComponent}-annotated {@link ColumnModule} classes as
 * bean definitions with constructor autowiring.
 *
 * <p>This is the Spring-aware counterpart of
 * {@link io.lark.column.biz.ColumnComponentScanner}; it uses Spring's classpath
 * scanner and lets the container resolve constructor dependencies.
 */
public final class ColumnComponentRegistrar implements ImportBeanDefinitionRegistrar,
        EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry) {
        Set<String> packages = new LinkedHashSet<>();
        addAnnotationPackages(metadata, packages);
        addPropertyPackages(packages);
        if (packages.isEmpty()) {
            return;
        }

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ColumnComponent.class));
        for (String packageName : packages) {
            for (BeanDefinition definition : scanner.findCandidateComponents(packageName)) {
                register(registry, definition);
            }
        }
    }

    private static void addAnnotationPackages(AnnotationMetadata metadata,
                                              Set<String> packages) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(
                EnableLaryColumns.class.getName());
        if (attributes == null) {
            return;
        }
        for (String packageName : (String[]) attributes.get("basePackages")) {
            if (packageName != null && !packageName.isBlank()) {
                packages.add(packageName);
            }
        }
        for (Class<?> type : (Class<?>[]) attributes.get("basePackageClasses")) {
            packages.add(ClassUtils.getPackageName(type));
        }
        if (packages.isEmpty()) {
            packages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
    }

    private void addPropertyPackages(Set<String> packages) {
        if (environment == null) {
            return;
        }
        Binder.get(environment)
                .bind("lary.column.scan-packages", Bindable.listOf(String.class))
                .ifBound(packages::addAll);
    }

    private static void register(BeanDefinitionRegistry registry,
                                 BeanDefinition definition) {
        String className = definition.getBeanClassName();
        if (className == null) {
            return;
        }
        Class<?> type;
        try {
            type = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Cannot load @ColumnComponent class " + className, e);
        }
        if (!ColumnModule.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(
                    "@ColumnComponent class must extend ColumnModule: " + className);
        }
        if (hasDefinitionFor(registry, className)) {
            return;
        }
        if (definition instanceof AbstractBeanDefinition beanDefinition) {
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        }
        registry.registerBeanDefinition(className, definition);
    }

    private static boolean hasDefinitionFor(BeanDefinitionRegistry registry,
                                            String className) {
        if (registry.containsBeanDefinition(className)) {
            return true;
        }
        for (String name : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(name);
            if (definition instanceof AbstractBeanDefinition beanDefinition
                    && className.equals(beanDefinition.getBeanClassName())) {
                return true;
            }
        }
        return false;
    }
}
