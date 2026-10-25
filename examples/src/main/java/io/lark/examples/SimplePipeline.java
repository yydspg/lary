// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples;

import io.lark.dag.DefaultContext;
import io.lark.dag.Executor;
import io.lark.dag.Graph;
import io.lark.dag.GraphBuilder;
import io.lark.dag.IContext;
import io.lark.dag.Node;
import io.lark.dag.NodeDef;
import io.lark.dag.NodeRegistry;
import io.lark.dag.StatsCollector;

/**
 * Node-layer quick start: read a seed and transform it.
 *
 * <p>Run with {@code ./gradlew :examples:runSimple}.
 */
public final class SimplePipeline {

    public static final class SourceNode extends Node {
        @Override
        public void compute(IContext ctx) {
            int seed = ctx.get("seed", Integer.class);
            ctx.set("value", seed);
        }
    }

    public static final class DoubleNode extends Node {
        @Override
        public void compute(IContext ctx) {
            int value = ctx.get("value", Integer.class);
            ctx.set("result", value * 2);
        }
    }

    private SimplePipeline() {
    }

    public static void main(String[] args) {
        NodeRegistry registry = new NodeRegistry();
        registry.register("source", SourceNode::new);
        registry.register("double", DoubleNode::new);

        Graph graph = new GraphBuilder(registry).build(
                new NodeDef("source"),
                new NodeDef("double", "source"));

        DefaultContext ctx = new DefaultContext();
        ctx.set("seed", 7);

        StatsCollector stats = new StatsCollector();
        try (Executor executor = new Executor(2, 4)) {
            executor.setMonitor(stats);
            executor.execute(graph, ctx);
        }

        System.out.println("value  = " + ctx.get("value", Integer.class));
        System.out.println("result = " + ctx.get("result", Integer.class));
        System.out.print(stats.stats().summary());
    }
}
