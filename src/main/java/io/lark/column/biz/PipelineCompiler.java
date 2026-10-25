// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.backend.Backend;
import io.lark.column.exec.ComputeGraph;
import io.lark.column.op.OpSpec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Turns business modules into an execution {@link ComputeGraph}.
 *
 * <p>Modules are compiled in registration order. A reference to a column (or a
 * {@code dependsOn} tail) produced by a module compiled later gets an
 * anonymous placeholder node, which is replaced by the real producer once that
 * module compiles. Unresolved forward references and cycles fail compilation.
 */
public final class PipelineCompiler {

    public static final class CompiledModule {
        private final String name;
        private final List<String> outputNames = new ArrayList<>();
        private final List<String> outputNodes = new ArrayList<>();
        private String tail;

        private CompiledModule(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        public List<String> outputNames() {
            return List.copyOf(outputNames);
        }

        public List<String> outputNodes() {
            return List.copyOf(outputNodes);
        }

        public String tail() {
            return tail;
        }
    }

    private final ComputeGraph graph;
    private final Backend backend;

    private final Map<String, String> columnProducer = new LinkedHashMap<>();
    private final Map<String, String> columnModule = new LinkedHashMap<>();
    private final Map<String, CompiledModule> moduleInfo = new LinkedHashMap<>();
    private final Map<String, String> columnPlaceholder = new LinkedHashMap<>();
    private final Map<String, String> moduleTailPlaceholder = new LinkedHashMap<>();

    public PipelineCompiler(ComputeGraph graph, Backend backend) {
        this.graph = graph;
        this.backend = backend;
    }

    /**
     * Compile modules in registration order.
     *
     * @throws IllegalArgumentException on ambiguous producers or unknown
     *     {@code dependsOn} targets
     * @throws IllegalStateException on unresolved forward references
     */
    public void compile(List<Module> modules, Set<String> moduleNames) {
        columnProducer.clear();
        columnModule.clear();
        moduleInfo.clear();
        columnPlaceholder.clear();
        moduleTailPlaceholder.clear();

        collectColumnProducers(modules);

        for (Module module : modules) {
            for (String dependency : module.dependencies()) {
                if (!moduleNames.contains(dependency)) {
                    throw new IllegalArgumentException(
                            "PipelineCompiler: module '" + module.name()
                                    + "' depends_on unknown module '" + dependency + "'");
                }
            }
        }

        for (Module module : modules) {
            compileModule(module);
        }

        if (graph.hasPlaceholders()) {
            throw new IllegalStateException(
                    "PipelineCompiler: unresolved forward references "
                            + "(a referenced column/tail was never produced)");
        }
    }

    public Map<String, String> columnProducers() {
        return Map.copyOf(columnProducer);
    }

    private void collectColumnProducers(List<Module> modules) {
        for (Module module : modules) {
            Set<String> written = new LinkedHashSet<>(module.outputs());
            for (OpSpec spec : module.subgraph().ops()) {
                written.addAll(spec.outputs());
            }
            for (String column : written) {
                if (column.isEmpty() || column.charAt(0) == '@') {
                    continue; // module-local temp
                }
                String previous = columnModule.putIfAbsent(column, module.name());
                if (previous != null && !previous.equals(module.name())) {
                    throw new IllegalArgumentException(
                            "PipelineCompiler: column '" + column
                                    + "' produced by both '" + previous
                                    + "' and '" + module.name() + "'");
                }
            }
        }
    }

    private void compileModule(Module module) {
        CompiledModule info = new CompiledModule(module.name());
        Map<String, String> local = new LinkedHashMap<>();
        Map<String, String> rename = new LinkedHashMap<>();
        NodeAdder adder = new NodeAdder(module.name());

        // Anonymous input boundary nodes.
        for (String port : module.inputs()) {
            String nodeId = module.name() + "/@in:" + port;
            adder.add(nodeId, new OpSpec("identity", List.of(port), List.of(port)));
            String dependency = resolveInputDep(port, local, module.name());
            if (!dependency.isEmpty()) {
                graph.addDependency(nodeId, dependency);
            }
            local.put(port, nodeId);
        }

        // Sub-graph ops.
        int index = 0;
        for (OpSpec spec : module.subgraph().ops()) {
            List<String> inputs = new ArrayList<>(spec.inputs().size());
            for (String input : spec.inputs()) {
                inputs.add(remap(input, module.name(), rename));
            }
            List<String> outputs = new ArrayList<>(spec.outputs().size());
            for (String output : spec.outputs()) {
                outputs.add(remap(output, module.name(), rename));
            }

            String nodeId = module.name() + "/op" + index++;
            adder.add(nodeId, new OpSpec(spec.type(), inputs, outputs, spec.attrs()));

            for (String input : inputs) {
                String dependency = resolveInputDep(input, local, module.name());
                if (!dependency.isEmpty()) {
                    graph.addDependency(nodeId, dependency);
                }
            }
            for (String output : outputs) {
                if (output.isEmpty()) {
                    continue;
                }
                if (local.containsKey(output)) {
                    throw new IllegalArgumentException(
                            "PipelineCompiler: column '" + output
                                    + "' written twice in module '" + module.name() + "'");
                }
                local.put(output, nodeId);
                registerProducer(output, nodeId, module.name());
            }
            info.tail = nodeId;
        }

        // Declared outputs. A pass-through output (an input port or a feed
        // column) gets an anonymous identity node.
        for (String port : module.outputs()) {
            String existing = local.get(port);
            if (existing != null) {
                info.outputNodes.add(existing);
            } else {
                String nodeId = module.name() + "/@out:" + port;
                adder.add(nodeId, new OpSpec("identity", List.of(port), List.of(port)));
                String dependency = resolveInputDep(port, local, module.name());
                if (!dependency.isEmpty()) {
                    graph.addDependency(nodeId, dependency);
                }
                local.put(port, nodeId);
                registerProducer(port, nodeId, module.name());
                info.outputNodes.add(nodeId);
                info.tail = nodeId;
            }
            info.outputNames.add(port);
        }

        // A tail is needed so a later module's forward dependsOn edge has a
        // real node to point at.
        if (info.tail == null) {
            String nodeId = module.name() + "/@noop";
            String noop = "@noop:" + module.name();
            adder.add(nodeId, new OpSpec("identity", List.of(noop), List.of(noop)));
            info.tail = nodeId;
        }

        // Explicit module-ordering edges.
        for (String dependencyName : module.dependencies()) {
            String nodeId = module.name() + "/@dep:" + dependencyName;
            adder.add(nodeId, new OpSpec("identity",
                    List.of(module.name()), List.of(module.name())));

            CompiledModule dependency = moduleInfo.get(dependencyName);
            if (dependency != null) {
                if (!dependency.outputNodes.isEmpty()) {
                    for (String outputNode : dependency.outputNodes) {
                        graph.addDependency(nodeId, outputNode);
                    }
                } else if (dependency.tail != null) {
                    graph.addDependency(nodeId, dependency.tail);
                }
            } else {
                String placeholder = moduleTailPlaceholder.computeIfAbsent(
                        dependencyName, name -> {
                            String id = "@tail:" + name;
                            graph.addPlaceholder("@tail:" + name, id);
                            return id;
                        });
                graph.addDependency(nodeId, placeholder);
            }

            if (adder.first != null && !adder.first.equals(nodeId)) {
                graph.addDependency(adder.first, nodeId);
            }
            if (info.tail == null) {
                info.tail = nodeId;
            }
        }

        String tailPlaceholder = moduleTailPlaceholder.remove(module.name());
        if (tailPlaceholder != null && info.tail != null) {
            graph.replacePlaceholder(tailPlaceholder, info.tail);
        }

        moduleInfo.put(module.name(), info);
    }

    private String resolveInputDep(String column, Map<String, String> local,
                                   String moduleName) {
        String localNode = local.get(column);
        if (localNode != null) {
            return localNode;
        }
        String producer = columnProducer.get(column);
        if (producer != null) {
            return producer;
        }
        String owner = columnModule.get(column);
        if (owner != null && !owner.equals(moduleName)) {
            return ensureColumnPlaceholder(column);
        }
        // Feed port: data is seeded before compute(), so no edge is required.
        return "";
    }

    private String ensureColumnPlaceholder(String column) {
        String existing = columnPlaceholder.get(column);
        if (existing != null) {
            return existing;
        }
        String id = "@placeholder:" + column;
        graph.addPlaceholder(column, id);
        columnPlaceholder.put(column, id);
        return id;
    }

    private void registerProducer(String column, String nodeId, String moduleName) {
        columnProducer.put(column, nodeId);
        columnModule.put(column, moduleName);
        String placeholder = columnPlaceholder.remove(column);
        if (placeholder != null) {
            graph.replacePlaceholder(placeholder, nodeId);
        }
    }

    private static String remap(String column, String moduleName,
                                Map<String, String> rename) {
        if (column.isEmpty() || column.charAt(0) != '@') {
            return column;
        }
        String existing = rename.get(column);
        if (existing != null) {
            return existing;
        }
        String mapped = "@" + moduleName + "/" + column.substring(1);
        rename.put(column, mapped);
        return mapped;
    }

    private final class NodeAdder {
        private final String moduleName;
        private String first;

        private NodeAdder(String moduleName) {
            this.moduleName = moduleName;
        }

        private void add(String id, OpSpec spec) {
            graph.addNode(id, moduleName, backend.createOp(spec));
            if (first == null) {
                first = id;
            }
        }
    }
}
