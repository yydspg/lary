// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.util.Locale;

/** Execution status of an op within one graph run. */
public enum OpStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    SKIPPED,
    FAILED;

    public String displayName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return displayName();
    }
}
