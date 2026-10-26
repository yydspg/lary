// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.MulNode;
import io.lark.examples.search.schema.SearchMetrics;

/** Click-through conversion rate, wired from the CTR and CVR modules. */
public final class CtCvrModule extends ColumnModule {

    private final MulNode ctcvr;

    public CtCvrModule(CtrModule ctr, CvrModule cvr) {
        ColumnStream<Double> ctrOut = ctr.published(SearchMetrics.CTR);
        ColumnStream<Double> cvrOut = cvr.published(SearchMetrics.CVR);
        ctcvr = node(new MulNode(SearchMetrics.CTCVR), ctrOut, cvrOut);
        dependsOn(CtrModule.class, CvrModule.class);
        output(ctcvr.out());
    }

    public ColumnStream<Double> ctcvr() {
        return ctcvr.out();
    }
}
