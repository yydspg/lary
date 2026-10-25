// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

/** What to do once a node/op repeatedly times out. */
public enum TimeoutAction {
    /** Quarantine the node/op so later runs skip it quickly. */
    QUARANTINE,
    /** Abort the whole current graph run, cancelling running nodes. */
    ABORT_GRAPH
}
