package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransferDTO {
    private int uid;
    private int toUid;
    private int channelId;
    private int channelType;
    private int type;
    private long amount;
}
