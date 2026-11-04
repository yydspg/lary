// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.rank;

/** Failure while building or executing a sequential ranking policy. */
public class RankingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RankingException(String message) {
        super(message);
    }

    public RankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
