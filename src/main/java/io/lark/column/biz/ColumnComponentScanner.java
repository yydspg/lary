// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Lightweight classpath scanner for {@link ColumnComponent}-annotated
 * {@link ColumnModule}s.
 *
 * <p>Scans class directories and jars; instantiates annotated modules via a
 * no-argument constructor. No Spring dependency is required. The optional
 * {@code :spring} module uses Spring's scanner and container instead, which
 * supports constructor injection.
 */
public final class ColumnComponentScanner {

    private ColumnComponentScanner() {
    }

    public static List<ColumnModule> scanModules(String... packageNames) {
        if (packageNames == null || packageNames.length == 0) {
            return List.of();
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ColumnComponentScanner.class.getClassLoader();
        }

        Set<Class<?>> classes = new LinkedHashSet<>();
        for (String packageName : packageNames) {
            if (packageName == null || packageName.isBlank()) {
                continue;
            }
            scanPackage(packageName, loader, classes);
        }

        List<Class<?>> sorted = new ArrayList<>(classes);
        sorted.sort((left, right) -> left.getName().compareTo(right.getName()));

        List<ColumnModule> modules = new ArrayList<>();
        for (Class<?> type : sorted) {
            ColumnComponent component = type.getAnnotation(ColumnComponent.class);
            if (component == null) {
                continue;
            }
            if (!ColumnModule.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(
                        "@ColumnComponent class must extend ColumnModule: "
                                + type.getName());
            }
            if (Modifier.isAbstract(type.getModifiers()) || type.isInterface()) {
                throw new IllegalArgumentException(
                        "@ColumnComponent class must be concrete: " + type.getName());
            }
            try {
                var constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                modules.add((ColumnModule) constructor.newInstance());
            } catch (ReflectiveOperationException e) {
                throw new IllegalArgumentException(
                        "Cannot instantiate @ColumnComponent " + type.getName()
                                + " (a no-argument constructor is required)", e);
            }
        }
        return modules;
    }

    private static void scanPackage(String packageName, ClassLoader loader,
                                    Set<Class<?>> classes) {
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = loader.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                switch (url.getProtocol()) {
                    case "file" -> scanDirectory(packageName, url, loader, classes);
                    case "jar" -> scanJar(packageName, url, loader, classes);
                    default -> {
                        // Unsupported classpath layout; ignore.
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to scan package '" + packageName + "'", e);
        }
    }

    private static void scanDirectory(String packageName, URL url,
                                      ClassLoader loader, Set<Class<?>> classes) {
        try {
            Path root = Paths.get(url.toURI());
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".class"))
                        .forEach(path -> {
                            String relative = root.relativize(path).toString();
                            String className = packageName + "."
                                    + relative.replace('/', '.')
                                    .replace('\\', '.')
                                    .replaceAll("\\.class$", "");
                            load(className, loader, classes);
                        });
            }
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Failed to scan directory " + url, e);
        }
    }

    private static void scanJar(String packageName, URL url,
                                ClassLoader loader, Set<Class<?>> classes) {
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            String prefix = packageName.replace('.', '/') + "/";
            try (JarFile jar = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (entry.isDirectory() || !name.startsWith(prefix)
                            || !name.endsWith(".class")) {
                        continue;
                    }
                    String className = name.replace('/', '.')
                            .replaceAll("\\.class$", "");
                    load(className, loader, classes);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan jar " + url, e);
        }
    }

    private static void load(String className, ClassLoader loader,
                             Set<Class<?>> classes) {
        if (className.endsWith("module-info")
                || className.endsWith("package-info")
                || className.matches(".*\\$\\d+$")) {
            return;
        }
        try {
            classes.add(Class.forName(className, false, loader));
        } catch (LinkageError | ClassNotFoundException ignored) {
            // Optional dependency missing; skip.
        }
    }
}
