// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.demo.dag;

import io.lark.dag.IContext;
import io.lark.dag.Node;
import io.lark.examples.search.SearchDemo;
import io.lark.examples.search.model.SearchRequest;

/** DAG node: simulate one request with a fixed candidate count. */
public final class LoadRequestNode extends Node {

    public static final String ID = "load_request";
    public static final String OUTPUT = "request";

    private final int candidateCount;

    public LoadRequestNode(int candidateCount) {
        if (candidateCount <= 0) {
            throw new IllegalArgumentException("candidateCount must be positive");
        }
        this.candidateCount = candidateCount;
    }

    @Override
    public void compute(IContext ctx) {
        ctx.set(OUTPUT, SearchDemo.simulate(SearchDemo.sampleContext(),
                candidateCount), SearchRequest.class);
    }
}
