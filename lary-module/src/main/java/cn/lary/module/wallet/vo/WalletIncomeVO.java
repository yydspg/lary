package cn.lary.module.wallet.vo;

import cn.lary.module.wallet.entity.WalletIncome;
import lombok.Data;

@Data
public class WalletIncomeVO {

    private long id;

    /**
     * id
     */
    private int toUid;

    /**
     * 频道id
     */
    private int channelId;

    /**
     * 频道类型
     */
    private int channelType;

    /**
     * 交易类型
     */
    private int type;

    /**
     * 花费
     */
    private long cost;

    public WalletIncomeVO(WalletIncome income) {
        this.id = income.getId();
        this.toUid = income.getToUid();
        this.channelId = income.getChannelId();
        this.channelType = income.getChannelType();
        this.type = income.getType();
        this.cost = income.getCost();
    }
}
