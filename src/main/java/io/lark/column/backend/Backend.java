// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.backend;

import io.lark.column.DType;
import io.lark.column.exec.OpSpec;
import io.lark.column.exec.TensorOp;

/** Pluggable compute backend. */
public interface Backend {

    String name();

    boolean supports(DType dtype);

    TensorOp createOp(OpSpec spec);
}
