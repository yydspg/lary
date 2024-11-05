package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class BatchOutcomeRandomTransferDTO {

    private long uid;
    private List<Long> recipients;
    private List<BigDecimal> amount;
    private BigDecimal totalAmount;
    private long channel;
    private int category;
    private int type;
    private int transfer;

}
