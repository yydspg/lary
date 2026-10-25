// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.op;

/**
 * Optional capability for an {@link OpTensor} that can degrade gracefully when
 * execution fails (business-node fallback).
 */
public interface FallibleOpTensor {

    /**
     * Produce a degraded result for the failed execution.
     *
     * @return {@code true} when a usable result was written into the context
     */
    boolean fallback(OpContext ctx, Throwable error);
}
