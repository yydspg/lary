// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

class OpDslTest {

    // ---- registered ops -------------------------------------------------

    static final class DslFetchOp extends Op {
        @Override
        public String name() {
            return "DslFetchOp";
        }

        @Override
        public void execute(IContext data) {
            data.set("x", data.get("base", Integer.class) + 1);
        }
    }

    static final class DslDoubleOp extends Op {
        @Override
        public String name() {
            return "DslDoubleOp";
        }

        @Override
        public void execute(IContext data) {
            data.set("y", data.get("x", Integer.class) * 2);
        }
    }

    static final class DslConstOp extends Op {
        @Override
        public String name() {
            return "DslConstOp";
        }

        @Override
        public void execute(IContext data) {
            data.set("c", 100);
        }
    }

    static final class DslSumOp extends Op {
        @Override
        public String name() {
            return "DslSumOp";
        }

        @Override
        public void execute(IContext data) {
            data.set("total", data.get("y", Integer.class) + data.get("c", Integer.class));
        }
    }

    private static OpRegistry registry() {
        OpRegistry registry = new OpRegistry();
        registry.register("DslFetchOp", DslFetchOp::new);
        registry.register("DslDoubleOp", DslDoubleOp::new);
        registry.register("DslConstOp", DslConstOp::new);
        registry.register("DslSumOp", DslSumOp::new);
        return registry;
    }

    // ---- tests ----------------------------------------------------------

    @Test
    void registryCollectsOpsByTypeName() {
        OpRegistry registry = registry();

        assertTrue(registry.contains("DslFetchOp"));
        assertTrue(registry.contains("DslSumOp"));
        assertFalse(registry.contains("NopeOp"));

        Op op = registry.create("DslConstOp", "any_id");
        assertNotNull(op);
        assertEquals("DslConstOp", op.name());
        assertEquals(4, registry.registeredTypes().size());

        assertThrows(NoSuchElementException.class,
                () -> registry.create("Ghost"));
        assertThrows(IllegalArgumentException.class,
                () -> registry.register("DslFetchOp", DslFetchOp::new));
    }

    @Test
    void kvBuildCreatesAndExecutesGraph() {
        OpGraphBuilder builder = new OpGraphBuilder(registry());

        OpGraph graph = builder.build(List.of(
                new OpDef("DslFetchOp", List.of(), "fetch"),
                new OpDef("DslDoubleOp", List.of("fetch"), "dbl"),
                new OpDef("DslFetchOp", List.of("dbl"), "fetch2")));

        assertEquals(3, graph.size());
        graph.compile();
        assertEquals(3, graph.ops().size());

        DefaultContext data = new DefaultContext();
        data.set("base", 10);
        try (OpExecutor executor = new OpExecutor()) {
            executor.execute(graph, data);
            assertTrue(executor.allSucceeded());
        }

        assertEquals(11, data.get("x", Integer.class));
        assertEquals(22, data.get("y", Integer.class));

        assertThrows(IllegalArgumentException.class,
                () -> builder.build(List.of(new OpDef("GhostOp"))));
        assertThrows(IllegalArgumentException.class,
                () -> builder.build(List.of(
                        new OpDef("DslFetchOp", List.of("nope")))));
    }

    @Test
    void dslBuildSupportsChainsFanInAndBareNames() {
        OpGraphBuilder builder = new OpGraphBuilder(registry());

        OpGraph graph = builder.buildDsl(
                "fetch:DslFetchOp -> dbl:DslDoubleOp -> total:DslSumOp\n"
                        + "c:DslConstOp -> total:DslSumOp\n");
        assertEquals(4, graph.size());
        graph.compile();

        DefaultContext data = new DefaultContext();
        data.set("base", 10);
        try (OpExecutor executor = new OpExecutor()) {
            executor.execute(graph, data);
            assertTrue(executor.allSucceeded());
        }

        assertEquals(11, data.get("x", Integer.class));
        assertEquals(22, data.get("y", Integer.class));
        assertEquals(100, data.get("c", Integer.class));
        assertEquals(122, data.get("total", Integer.class));

        OpGraph bare = builder.buildDsl("DslConstOp -> DslSumOp");
        assertEquals(2, bare.size());

        OpGraph commented = builder.buildDsl("# leading comment\nDslConstOp");
        assertEquals(1, commented.size());
    }

    @Test
    void dslRejectsTypeConflictsUnknownTypesAndCycles() {
        OpGraphBuilder builder = new OpGraphBuilder(registry());

        assertThrows(IllegalArgumentException.class,
                () -> builder.buildDsl("a:DslConstOp -> a:DslDoubleOp"));
        assertThrows(IllegalArgumentException.class,
                () -> builder.buildDsl("x:NoSuchOp"));

        OpGraph cycle = builder.buildDsl(
                "a:DslFetchOp -> b:DslDoubleOp -> a:DslFetchOp");
        assertThrows(IllegalStateException.class, cycle::compile);

        assertThrows(IllegalArgumentException.class,
                () -> builder.buildDsl("a:DslFetchOp ->"));
    }
}
