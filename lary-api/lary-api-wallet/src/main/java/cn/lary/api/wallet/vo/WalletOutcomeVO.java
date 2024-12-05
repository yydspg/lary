package cn.lary.api.wallet.vo;

import cn.lary.api.wallet.entity.WalletOutcome;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletOutcomeVO {


    private long id;

    /**
     * id
     */
    private long toUid;

    /**
     * 频道id
     */
    private long channel;

    /**
     * 频道类型
     */
    private int category;

    /**
     * 交易类型
     */
    private int type;

    /**
     * 花费
     */
    private BigDecimal amount;
    
    public WalletOutcomeVO(WalletOutcome outcome) {
        this.id = outcome.getId();
        this.toUid = outcome.getToUid();
        this.channel = outcome.getChannel();
        this.category = outcome.getCategory();
        this.type = outcome.getType();
        this.amount = outcome.getAmount();
    }
}
