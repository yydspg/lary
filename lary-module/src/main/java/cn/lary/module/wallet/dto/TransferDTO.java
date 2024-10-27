package cn.lary.module.wallet.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransferDTO {
    private long uid;
    private long toUid;
    private long channelId;
    private int channelType;
    private int type;
    private long amount;
}
