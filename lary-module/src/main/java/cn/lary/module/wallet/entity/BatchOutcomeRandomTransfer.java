package cn.lary.module.wallet.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BatchOutcomeRandomTransfer {

    private long user;

    private BigDecimal amount;
}
