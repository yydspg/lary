// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

/** A node/operator failed while the pipeline was executing. */
public class ColumnExecutionException extends ColumnException {

    private static final long serialVersionUID = 1L;

    public ColumnExecutionException(String message) {
        super(message);
    }

    public ColumnExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
