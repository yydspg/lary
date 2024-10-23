package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SystemOutcomeTransferDTO {
    private List<Integer> members;
    private int type;
    private long amount;
}
