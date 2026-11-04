// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

/** Base type for column-engine failures raised by the framework. */
public class ColumnException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ColumnException(String message) {
        super(message);
    }

    public ColumnException(String message, Throwable cause) {
        super(message, cause);
    }
}
