// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

/** Invalid graph/module wiring detected before execution. */
public class ColumnDefinitionException extends ColumnException {

    private static final long serialVersionUID = 1L;

    public ColumnDefinitionException(String message) {
        super(message);
    }

    public ColumnDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
