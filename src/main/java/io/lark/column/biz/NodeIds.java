// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

import java.util.concurrent.atomic.AtomicLong;

/** Framework-generated unique IDs for nodes and modules. */
final class NodeIds {

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private NodeIds() {
    }

    static String next(Class<?> type) {
        String simple = type.getSimpleName();
        if (simple.isEmpty()) {
            simple = type.getName();
        }
        return simple + "#" + SEQUENCE.incrementAndGet();
    }
}
