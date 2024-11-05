package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TransferDTO {
    private long uid;
    private long toUid;
    private long channel;
    private int category;
    private int type;
    private int transfer;
    private BigDecimal amount;
}
