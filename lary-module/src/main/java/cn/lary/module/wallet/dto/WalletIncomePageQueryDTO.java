package cn.lary.module.wallet.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

@Data
public class WalletIncomePageQueryDTO extends PageQuery {
    private int uid;
    private Long cost;
    private Integer toUid;
    private Integer channelId;
    private Integer type;
}
