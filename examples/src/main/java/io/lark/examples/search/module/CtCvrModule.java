// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.search.module;

import io.lark.column.biz.ColumnComponent;
import io.lark.column.biz.ColumnModule;
import io.lark.column.biz.ColumnStream;
import io.lark.examples.search.node.SearchOps;
import io.lark.examples.search.schema.SearchMetrics;

/** Click-through conversion rate: {@code ctcvrNode = ctrNode * cvrNode}. */
@ColumnComponent
public final class CtCvrModule extends ColumnModule {

    private final CtrModule ctr;
    private final CvrModule cvr;
    private ColumnStream<Double> ctcvr;

    public CtCvrModule(CtrModule ctr, CvrModule cvr) {
        this.ctr = ctr;
        this.cvr = cvr;
    }

    @Override
    protected void define() {
        ctcvr = node(SearchOps.mul(SearchMetrics.CTCVR),
                ctr.published(SearchMetrics.CTR),
                cvr.published(SearchMetrics.CVR));
        dependsOn(CtrModule.class, CvrModule.class);
        output(ctcvr);
    }

    public ColumnStream<Double> ctcvr() {
        return ctcvr;
    }
}
