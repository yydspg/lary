package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class BatchOutcomeTransferDTO {
    private int uid;
    private List<Integer> recipients;
    private long amount;
    private long totalAmount;
    private int channelId;
    private int channelType;
    private int type;
}
