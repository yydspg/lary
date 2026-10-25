// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.exec;

import java.util.List;

/** Execution-layer computation bound to a compute node. */
public interface TensorOp {

    String opType();

    List<String> inputs();

    List<String> outputs();

    void compute(OpContext ctx);
}
