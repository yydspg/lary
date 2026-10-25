// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.examples.risk.columns;

import io.lark.column.biz.LongColumn;

/** External transaction count; the physical name contains digits. */
public final class TxnCount24h extends LongColumn {

    public TxnCount24h() {
        super("txn_count_24h");
    }
}
