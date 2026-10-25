// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.backend;

import io.lark.column.DType;
import io.lark.column.op.OpSpec;
import io.lark.column.op.OpTensor;

/** Pluggable compute backend. */
public interface Backend {

    String name();

    boolean supports(DType dtype);

    OpTensor createOp(OpSpec spec);
}
