// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.exec.ComputeGraph;
import io.lark.column.op.OpSpec;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates modules and compiles their typed streams into an immutable
 * {@link PipelinePlan}.
 *
 * <p>Compilation is deliberately pure with respect to {@link ColumnPipeline}:
 * it reads module definitions and either returns a fully built plan or throws,
 * leaving the previous plan untouched. This makes recompilation safe and keeps
 * the pipeline class focused on run orchestration.
 */
final class PipelineCompiler {

    private PipelineCompiler() {
    }

    static PipelinePlan compile(List<ColumnModule> modules,
                                Collection<String> requiredOutputs) {
        List<ColumnModule> moduleList = List.copyOf(modules);
        for (ColumnModule module : moduleList) {
            module.initialize();
        }

        Map<Class<? extends ColumnModule>, ColumnModule> modulesByType =
                new LinkedHashMap<>();
        for (ColumnModule module : moduleList) {
            ColumnModule previous = modulesByType.put(module.getClass(), module);
            if (previous != null && previous != module) {
                throw new IllegalArgumentException(
                        "ColumnPipeline: duplicate module type "
                                + module.getClass().getName());
            }
        }
        validateModuleDependencies(moduleList, modulesByType);

        Map<String, ColumnStream<?>> feedDefinitions = new LinkedHashMap<>();
        for (ColumnModule module : moduleList) {
            for (ColumnStream<?> input : module.inputView()) {
                ColumnStream<?> previous =
                        feedDefinitions.putIfAbsent(input.name(), input);
                if (previous != null
                        && !previous.definition().equals(input.definition())) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: feed column '" + input.name()
                                    + "' declared with conflicting definitions");
                }
            }
        }

        Map<ColumnNode, ColumnModule> nodeModules = new IdentityHashMap<>();
        Map<String, ColumnNode> producers = new LinkedHashMap<>();
        Map<ColumnStream<?>, ColumnModule> moduleInputs = new IdentityHashMap<>();
        List<ColumnNode> allNodes = new ArrayList<>();

        for (ColumnModule module : moduleList) {
            for (ColumnStream<?> input : module.inputView()) {
                moduleInputs.put(input, module);
            }
            for (ColumnNode node : module.nodeView()) {
                allNodes.add(node);
                if (nodeModules.put(node, module) != null) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: node added to multiple modules "
                                    + node.id());
                }
                for (ColumnStream<?> fetch : node.fetchView()) {
                    ColumnNode previous = producers.putIfAbsent(fetch.name(), node);
                    if (previous != null && previous != node) {
                        throw new IllegalArgumentException(
                                "ColumnPipeline: stream '" + fetch.name()
                                        + "' produced by both " + previous.id()
                                        + " and " + node.id());
                    }
                }
            }
        }

        Map<ColumnNode, List<ColumnNode>> deps = new IdentityHashMap<>();
        for (ColumnNode node : allNodes) {
            deps.put(node, new ArrayList<>());
            for (ColumnStream<?> feed : node.feedView()) {
                ColumnNode producer = feed.producer().orElse(null);
                if (producer != null) {
                    if (!nodeModules.containsKey(producer)) {
                        throw new IllegalArgumentException(
                                "ColumnPipeline: stream '" + feed.name()
                                        + "' of node " + node.id()
                                        + " has a producer outside this pipeline");
                    }
                    deps.get(node).add(producer);
                } else {
                    if (!moduleInputs.containsKey(feed)) {
                        throw new IllegalArgumentException(
                                "ColumnPipeline: stream '" + feed.name()
                                        + "' of node " + node.id()
                                        + " is not declared as a module input");
                    }
                }
            }
        }

        Map<String, ColumnStream<?>> allOutputs = new LinkedHashMap<>();
        for (ColumnModule module : moduleList) {
            for (ColumnStream<?> output : module.outputView()) {
                ColumnStream<?> previous =
                        allOutputs.putIfAbsent(output.name(), output);
                if (previous != null && previous != output) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: duplicate module output stream '"
                                    + output.name() + "'");
                }
            }
        }

        Set<ColumnNode> kept;
        List<String> outputs;
        if (requiredOutputs == null || requiredOutputs.isEmpty()) {
            kept = new LinkedHashSet<>(allNodes);
            outputs = List.copyOf(allOutputs.keySet());
        } else {
            outputs = List.copyOf(requiredOutputs);
            kept = requiredNodes(outputs, allOutputs, deps, moduleInputs);
        }

        Set<String> feedColumns = new LinkedHashSet<>();
        for (ColumnNode node : kept) {
            for (ColumnStream<?> feed : node.feedView()) {
                if (feed.producer().isEmpty()) {
                    feedColumns.add(feed.name());
                }
            }
        }
        for (String output : outputs) {
            ColumnStream<?> stream = allOutputs.get(output);
            if (stream != null && stream.producer().isEmpty()) {
                feedColumns.add(stream.name());
            }
        }

        ComputeGraph graph = new ComputeGraph();
        for (ColumnNode node : kept) {
            ColumnModule module = nodeModules.get(node);
            graph.addNode(node.id(), module.id(),
                    new OpSpec("business", names(node.feedView()),
                            names(node.fetchView()), Map.of()),
                    new BusinessNodeKernel(node));
        }
        for (ColumnNode node : kept) {
            for (ColumnNode dependency : deps.get(node)) {
                if (kept.contains(dependency)) {
                    graph.addDependency(node.id(), dependency.id());
                }
            }
        }
        graph.finalizeGraph();

        return new PipelinePlan(graph, feedColumns,
                List.copyOf(feedColumns), feedDefinitions, outputs,
                Set.copyOf(outputs), kept.size());
    }

    private static void validateModuleDependencies(
            List<ColumnModule> modules,
            Map<Class<? extends ColumnModule>, ColumnModule> modulesByType) {
        Map<Class<? extends ColumnModule>, Integer> inDegree =
                new LinkedHashMap<>();
        Map<Class<? extends ColumnModule>, List<Class<? extends ColumnModule>>> outgoing =
                new LinkedHashMap<>();
        for (ColumnModule module : modules) {
            inDegree.put(module.getClass(), 0);
        }
        for (ColumnModule module : modules) {
            for (Class<? extends ColumnModule> dependency : module.dependencyView()) {
                if (!modulesByType.containsKey(dependency)) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: module " + module.getClass().getName()
                                    + " depends on missing module "
                                    + dependency.getName());
                }
                outgoing.computeIfAbsent(dependency, key -> new ArrayList<>())
                        .add(module.getClass());
                inDegree.merge(module.getClass(), 1, Integer::sum);
            }
        }
        Deque<Class<? extends ColumnModule>> ready = new ArrayDeque<>();
        for (Map.Entry<Class<? extends ColumnModule>, Integer> entry
                : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                ready.add(entry.getKey());
            }
        }
        int visited = 0;
        while (!ready.isEmpty()) {
            Class<? extends ColumnModule> type = ready.poll();
            visited++;
            for (Class<? extends ColumnModule> next
                    : outgoing.getOrDefault(type, List.of())) {
                int degree = inDegree.merge(next, -1, Integer::sum);
                if (degree == 0) {
                    ready.add(next);
                }
            }
        }
        if (visited != modules.size()) {
            throw new IllegalStateException(
                    "ColumnPipeline: module dependency cycle detected");
        }
    }

    private static List<String> names(ColumnStream<?>[] streams) {
        List<String> names = new ArrayList<>(streams.length);
        for (ColumnStream<?> stream : streams) {
            names.add(stream.name());
        }
        return names;
    }

    private static Set<ColumnNode> requiredNodes(
            List<String> requiredOutputs,
            Map<String, ColumnStream<?>> allOutputs,
            Map<ColumnNode, List<ColumnNode>> deps,
            Map<ColumnStream<?>, ColumnModule> moduleInputs) {
        Set<ColumnNode> needed = new LinkedHashSet<>();
        Deque<ColumnNode> pending = new ArrayDeque<>();

        for (String output : requiredOutputs) {
            ColumnStream<?> stream = allOutputs.get(output);
            if (stream == null) {
                throw new IllegalArgumentException(
                        "ColumnPipeline: unknown fetch output '" + output + "'");
            }
            ColumnNode producer = stream.producer().orElse(null);
            if (producer == null) {
                if (!moduleInputs.containsKey(stream)) {
                    throw new IllegalArgumentException(
                            "ColumnPipeline: fetch output '" + output
                                    + "' is not a module output or input");
                }
                continue;
            }
            if (needed.add(producer)) {
                pending.push(producer);
            }
        }

        while (!pending.isEmpty()) {
            ColumnNode node = pending.pop();
            for (ColumnNode dependency : deps.get(node)) {
                if (needed.add(dependency)) {
                    pending.push(dependency);
                }
            }
        }
        return needed;
    }
}
