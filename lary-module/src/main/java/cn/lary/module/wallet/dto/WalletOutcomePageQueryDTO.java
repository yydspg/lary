package cn.lary.module.wallet.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

@Data
public class WalletOutcomePageQueryDTO extends PageQuery {
    private Long cost;
    private Integer toUid;
    private Integer channelId;
    private Integer type;
}
