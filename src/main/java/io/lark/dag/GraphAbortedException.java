// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

/** Raised when the current graph run was cancelled by the framework. */
public class GraphAbortedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GraphAbortedException(String reason) {
        super("DAG execution aborted: " + reason);
    }
}
