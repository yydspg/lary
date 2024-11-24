package cn.lary.wallet.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

@Data
public class WalletIncomePageQueryDTO extends PageQuery {
    private long uid;
    private Long cost;
    private Long toUid;
    private Long channelId;
    private Integer type;
}
