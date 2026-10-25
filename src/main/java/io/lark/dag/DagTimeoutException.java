// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.dag;

import java.time.Duration;
import java.util.Objects;

/** Raised when a node/op exceeds its deadline. */
public class DagTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String subject;
    private final Duration timeout;
    private final boolean quarantined;

    public DagTimeoutException(String subject, Duration timeout, boolean quarantined) {
        super("DAG timeout: '" + subject + "' exceeded " + timeout
                + (quarantined ? " (quarantined)" : ""));
        this.subject = Objects.requireNonNull(subject, "subject");
        this.timeout = Objects.requireNonNull(timeout, "timeout");
        this.quarantined = quarantined;
    }

    public String subject() {
        return subject;
    }

    public Duration timeout() {
        return timeout;
    }

    public boolean quarantined() {
        return quarantined;
    }
}
