// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.backend;

import io.lark.column.tensor.DType;
import io.lark.column.tensor.TensorKernel;
import io.lark.column.op.OpContext;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;
import io.lark.column.op.TensorOps;

/** Pluggable compute backend. */
public interface Backend {

    String name();

    boolean supports(DType dtype);

    /** Create the execution kernel for a graph-level op. */
    default TensorKernel createKernel(OpSpec spec) {
        return ctx -> createOp(spec).compute(new OpContext(ctx.store()));
    }

    /** Compatibility pre-kernel API. */
    @Deprecated
    default OpTensor createOp(OpSpec spec) {
        return TensorOps.create(spec);
    }
}
