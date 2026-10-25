// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

/** Classification of worker pools exposed to business code. */
public enum PoolKind {
    /** CPU-bound work (no blocking syscalls). */
    COMPUTE,
    /** I/O-bound work (network, disk, sleeps, ...). */
    IO,
    /** Low-priority background tasks (logging, metrics, ...). */
    BACKGROUND
}
