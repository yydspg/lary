// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.dag;

import io.lark.dag.IContext;
import io.lark.dag.Node;
import io.lark.examples.demo.model.Prepared;
import io.lark.examples.demo.model.RankConfig;
import io.lark.examples.search.model.SearchRequest;

/** DAG node: validate the request/config pair and publish the typed result. */
public final class ValidateNode extends Node {

    public static final String ID = "validate";
    public static final String OUTPUT = "prepared";

    @Override
    public void compute(IContext ctx) {
        SearchRequest request = ctx.get(LoadRequestNode.OUTPUT,
                SearchRequest.class);
        RankConfig config = ctx.get(LoadConfigNode.OUTPUT, RankConfig.class);
        if (request.goods().size() == 0) {
            throw new IllegalArgumentException("no candidates");
        }
        ctx.set(OUTPUT, new Prepared(request, config, null), Prepared.class);
    }
}
