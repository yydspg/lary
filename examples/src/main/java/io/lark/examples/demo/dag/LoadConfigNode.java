// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.dag;

import io.lark.dag.IContext;
import io.lark.dag.Node;
import io.lark.examples.demo.model.RankConfig;

/** DAG node: load request-scoped ranking configuration. */
public final class LoadConfigNode extends Node {

    public static final String ID = "load_config";
    public static final String OUTPUT = "config";

    @Override
    public void compute(IContext ctx) {
        ctx.set(OUTPUT, new RankConfig(30, 5_000.0, 8), RankConfig.class);
    }
}
