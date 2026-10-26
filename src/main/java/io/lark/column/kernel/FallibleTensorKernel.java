// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.kernel;

/** Optional degradation hook for kernels used by business nodes. */
public interface FallibleTensorKernel {

    /**
     * Write a degraded result after kernel execution failed.
     *
     * @return {@code true} when a usable result was written
     */
    boolean fallback(KernelContext ctx, Throwable error);
}
