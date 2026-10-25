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

import java.util.List;

/**
 * Node-layer quick start: fetch a user, then rank it.
 *
 * <p>Run with {@code ./gradlew :examples:runSimple}.
 */
public final class SimplePipeline {

    public static final class FetchUserNode extends Node {
        @Override
        public void compute(IContext ctx) {
            int id = ctx.get("user_id", Integer.class);
            ctx.set("user", "user_" + id);
        }
    }

    public static final class RankNode extends Node {
        @Override
        public void compute(IContext ctx) {
            String user = ctx.get("user", String.class);
            ctx.set("rank", user.length());
        }
    }

    private SimplePipeline() {
    }

    public static void main(String[] args) {
        NodeRegistry registry = new NodeRegistry();
        registry.register("fetch_user", FetchUserNode::new);
        registry.register("rank", RankNode::new);

        Graph graph = new GraphBuilder(registry).build(
                new NodeDef("fetch_user"),
                new NodeDef("rank", List.of("fetch_user")));

        DefaultContext ctx = new DefaultContext();
        ctx.set("user_id", 7);

        StatsCollector stats = new StatsCollector();
        try (Executor executor = new Executor(2, 4)) {
            executor.setMonitor(stats);
            executor.execute(graph, ctx);
        }

        System.out.println("user = " + ctx.get("user", String.class));
        System.out.println("rank = " + ctx.get("rank", Integer.class));
        System.out.print(stats.stats().summary());
    }
}
