// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Declarative graph building for {@link Op} — KV form and a small DSL.
 *
 * <p>Ops are collected by type name via {@link OpRegistry}; the builder
 * instantiates them by name, so graphs can be described without touching the
 * op classes:
 *
 * <pre>{@code
 * OpGraph graph = new OpGraphBuilder()
 *         .buildDsl("fetch:DataFetchOp -> select:SelectOp -> rank:RankOp");
 * }</pre>
 *
 * <p>KV form:
 *
 * <pre>{@code
 * new OpGraphBuilder().build(List.of(
 *     new OpDef("DataFetchOp"),
 *     new OpDef("SelectOp", List.of("DataFetchOp"), "select")));
 * }</pre>
 */
public final class OpGraphBuilder {

    private final OpRegistry registry;
    private final List<OpAspect> aspects = new ArrayList<>();

    public OpGraphBuilder() {
        this(OpRegistry.instance());
    }

    public OpGraphBuilder(OpRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public OpGraphBuilder addAspect(OpAspect aspect) {
        if (aspect != null) {
            aspects.add(aspect);
        }
        return this;
    }

    /** KV form: build a graph from node definitions. */
    public OpGraph build(List<OpDef> defs) {
        Objects.requireNonNull(defs, "defs");

        OpGraph graph = new OpGraph();
        for (OpAspect aspect : aspects) {
            graph.addAspect(aspect);
        }

        Map<String, Op> byId = new LinkedHashMap<>();
        for (OpDef def : defs) {
            if (def.type().isEmpty()) {
                throw new IllegalArgumentException(
                        "OpGraphBuilder: node def has empty type");
            }
            String id = def.id().isEmpty() ? def.type() : def.id();
            Op op;
            try {
                op = registry.create(def.type(), id);
            } catch (java.util.NoSuchElementException e) {
                throw new IllegalArgumentException(
                        "OpGraphBuilder: unknown op type '" + def.type() + "'", e);
            }
            if (byId.putIfAbsent(id, op) != null) {
                throw new IllegalArgumentException(
                        "OpGraphBuilder: duplicate node id '" + id + "'");
            }
            graph.addOp(op, id);
        }

        for (OpDef def : defs) {
            String id = def.id().isEmpty() ? def.type() : def.id();
            for (String dep : def.deps()) {
                if (!byId.containsKey(dep)) {
                    throw new IllegalArgumentException(
                            "OpGraphBuilder: node '" + id
                                    + "' depends on unknown id '" + dep + "'");
                }
                graph.dependsOn(dep, id);
            }
        }

        return graph;
    }

    /**
     * DSL form: {@code "id:Type -> id:Type -> ..."} (statements separated by
     * {@code ;} or newline; reusing a node id merges dependencies — fan-in).
     */
    public OpGraph buildDsl(String dsl) {
        Objects.requireNonNull(dsl, "dsl");

        DslParser parser = new DslParser(dsl);
        List<PendingDef> pending = new ArrayList<>();
        Map<String, PendingDef> byId = new LinkedHashMap<>();

        while (!parser.atEnd()) {
            DslParser.Segment first = parser.segment();
            ensure(byId, pending, first);
            String previous = first.id();

            while (parser.matchSymbol("->")) {
                DslParser.Segment next = parser.segment();
                PendingDef target = ensure(byId, pending, next);
                target.deps.add(previous);
                previous = next.id();
            }

            parser.matchSymbol(";");
        }

        List<OpDef> defs = pending.stream()
                .map(PendingDef::toDef)
                .toList();
        return build(defs);
    }

    private static PendingDef ensure(Map<String, PendingDef> byId,
                                     List<PendingDef> pending,
                                     DslParser.Segment segment) {
        PendingDef existing = byId.get(segment.id());
        if (existing != null) {
            if (!existing.type.equals(segment.type())) {
                throw new IllegalArgumentException(
                        "OpGraphBuilder DSL: node id '" + segment.id()
                                + "' used with two different types");
            }
            return existing;
        }
        PendingDef created = new PendingDef(segment.id(), segment.type());
        byId.put(segment.id(), created);
        pending.add(created);
        return created;
    }

    private static final class PendingDef {
        private final String id;
        private final String type;
        private final List<String> deps = new ArrayList<>();

        private PendingDef(String id, String type) {
            this.id = id;
            this.type = type;
        }

        private OpDef toDef() {
            return new OpDef(type, deps, id);
        }
    }
}
