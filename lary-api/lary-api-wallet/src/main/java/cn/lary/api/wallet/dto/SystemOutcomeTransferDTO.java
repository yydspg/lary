package cn.lary.api.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class SystemOutcomeTransferDTO {
    private List<Long> members;
    private int type;
    private BigDecimal amount;
    private int transfer;
}
