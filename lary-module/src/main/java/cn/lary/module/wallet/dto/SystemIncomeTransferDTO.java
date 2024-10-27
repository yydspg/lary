package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SystemIncomeTransferDTO {
    private List<Long> members;
    private int type;
    private long amount;
}
