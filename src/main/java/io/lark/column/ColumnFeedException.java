// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

/** Feed data is missing, mismatched or cannot be mapped to columns. */
public class ColumnFeedException extends ColumnException {

    private static final long serialVersionUID = 1L;

    public ColumnFeedException(String message) {
        super(message);
    }

    public ColumnFeedException(String message, Throwable cause) {
        super(message, cause);
    }
}
