package cn.lary.api.wallet.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

@Data
public class WalletOutcomePageQueryDTO extends PageQuery {
    private Long cost;
    private Long toUid;
    private Long channelId;
    private Integer type;
}
