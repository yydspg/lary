package cn.lary.module.wallet.vo;

import lombok.Data;

@Data
public class WalletIncomeVO {

    private Long id;



    /**
     * id
     */
    private Integer toUid;

    /**
     * 频道id
     */
    private Integer channelId;

    /**
     * 频道类型
     */
    private Integer channelType;

    /**
     * 交易类型
     */
    private Integer type;

    /**
     * 花费
     */
    private Long cost;
}
