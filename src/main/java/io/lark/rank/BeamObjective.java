// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

import java.util.List;

/**
 * Objective used to rank partial sequences during beam search.
 *
 * <p>The default sums the selected slot scores. Business code can override it
 * to optimize revenue, diversity, GMV or any other global objective.
 */
@FunctionalInterface
public interface BeamObjective<T, C> {

    double score(List<RankedSlot<T>> selected, C businessContext);

    static <T, C> BeamObjective<T, C> sumOfScores() {
        return (selected, businessContext) -> {
            double total = 0.0;
            for (RankedSlot<T> slot : selected) {
                total += slot.score();
            }
            return total;
        };
    }
}
