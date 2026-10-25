// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.column;

import java.util.List;

/** Row-oriented business record. */
public record Row(List<Cell> cells) {

    public Row {
        cells = List.copyOf(cells);
    }

    public static Row of(Cell... cells) {
        return new Row(List.of(cells));
    }

    public Cell get(int index) {
        return cells.get(index);
    }

    public int size() {
        return cells.size();
    }
}
