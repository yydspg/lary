package cn.lary.module.wallet.vo;

import cn.lary.module.wallet.entity.WalletOutcome;
import lombok.Data;

@Data
public class WalletOutcomeVO {

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
    
    public WalletOutcomeVO(WalletOutcome outcome) {
        this.id = outcome.getId();
        this.toUid = outcome.getToUid();
        this.channelId = outcome.getChannelId();
        this.channelType = outcome.getChannelType();
        this.type = outcome.getType();
        this.cost = outcome.getCost();
    }
}
