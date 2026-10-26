// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import io.lark.column.tensor.DType;

/** Boolean formula expression represented as an INT64 0/1 mask. */
public interface BoolExpr extends Expr<Long> {

    @Override
    default DType dtype() {
        return DType.INT64;
    }

    default BoolExpr and(BoolExpr other) {
        return F.and(this, other);
    }

    default BoolExpr or(BoolExpr other) {
        return F.or(this, other);
    }

    default BoolExpr not() {
        return F.not(this);
    }
}
