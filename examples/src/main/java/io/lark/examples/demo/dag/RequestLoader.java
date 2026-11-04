// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.dag;

import io.lark.dag.DefaultContext;
import io.lark.dag.Executor;
import io.lark.dag.Graph;
import io.lark.dag.GraphBuilder;
import io.lark.dag.NodeDef;
import io.lark.dag.NodeRegistry;
import io.lark.dag.StatsCollector;
import io.lark.examples.demo.model.Prepared;

/**
 * DAG stage: load the request and config concurrently, then validate.
 */
public final class RequestLoader {

    private RequestLoader() {
    }

    public static Prepared load(int candidateCount) {
        NodeRegistry registry = new NodeRegistry();
        registry.register(LoadRequestNode.ID,
                () -> new LoadRequestNode(candidateCount));
        registry.register(LoadConfigNode.ID, LoadConfigNode::new);
        registry.register(ValidateNode.ID, ValidateNode::new);

        Graph graph = new GraphBuilder(registry).build(
                new NodeDef(LoadRequestNode.ID),
                new NodeDef(LoadConfigNode.ID),
                NodeDef.of(ValidateNode.ID, ValidateNode.ID,
                        LoadRequestNode.ID, LoadConfigNode.ID));

        StatsCollector stats = new StatsCollector();
        DefaultContext context = new DefaultContext();
        try (Executor executor = new Executor(2, 2)) {
            executor.setMonitor(stats);
            executor.execute(graph, context);
        }
        Prepared prepared = context.get(ValidateNode.OUTPUT, Prepared.class);
        return prepared.withStats(stats.stats());
    }
}
