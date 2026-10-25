// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.fixture;

import io.lark.column.DType;
import io.lark.column.biz.Column;

/** Minimal schema for the Spring integration tests. */
public final class NumberMetrics {

    public static final Column<Long> LEFT =
            new Column<>("left", DType.INT64);
    public static final Column<Long> RIGHT =
            new Column<>("right", DType.INT64);
    public static final Column<Long> SUM =
            new Column<>("sum", DType.INT64);
    public static final Column<Long> DOUBLED =
            new Column<>("doubled", DType.INT64);

    private NumberMetrics() {
    }
}
