// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column.biz;

/** Derives physical stream names from column class names. */
final class ColumnNames {

    private ColumnNames() {
    }

    /**
     * {@code AmountYuan → amount_yuan}, {@code UserID → user_id},
     * {@code HTTPStatus → http_status}.
     */
    static String of(Class<?> type) {
        String simple = type.getSimpleName();
        if (simple.isEmpty()) {
            throw new IllegalArgumentException(
                    "Column class must have a simple name: " + type.getName());
        }
        StringBuilder out = new StringBuilder(simple.length() + 4);
        for (int i = 0; i < simple.length(); i++) {
            char c = simple.charAt(i);
            if (Character.isUpperCase(c)) {
                char previous = i == 0 ? 0 : simple.charAt(i - 1);
                boolean previousBoundary = Character.isLowerCase(previous)
                        || Character.isDigit(previous);
                boolean acronymTail = i + 1 < simple.length()
                        && Character.isLowerCase(simple.charAt(i + 1));
                if (i > 0 && previousBoundary
                        && !(Character.isDigit(previous) && acronymTail)) {
                    out.append('_');
                }
                out.append(Character.toLowerCase(c));
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
