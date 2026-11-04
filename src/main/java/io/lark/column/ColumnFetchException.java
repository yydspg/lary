// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

/** A requested fetch column is unknown or was pruned from the plan. */
public class ColumnFetchException extends ColumnException {

    private static final long serialVersionUID = 1L;

    public ColumnFetchException(String message) {
        super(message);
    }

    public ColumnFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
