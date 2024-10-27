package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BatchOutcomeTransferDTO {
    private long uid;
    private List<Long> recipients;
    private long amount;
    private long totalAmount;
    private long channelId;
    private int channelType;
    private int type;
}
