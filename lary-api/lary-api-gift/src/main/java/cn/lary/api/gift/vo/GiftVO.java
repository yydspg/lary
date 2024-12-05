package cn.lary.api.gift.vo;

import cn.lary.api.gift.entity.GiftCache;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GiftVO {
    /**
     * gift type id
     */
    private int typeId;

    /**
     * virtual currency price
     */
    @JSONField(format="vc_amount")
    private long vcAmount;

    /**
     * real pay price CNY
     */
    @JSONField(format="amount")
    private BigDecimal amount;

    private String name;

    /**
     * gift logo
     */
    private String avatar;

    public GiftVO(){}

    public GiftVO(GiftCache cache) {

        this.amount = cache.getAmount();
        this.vcAmount = cache.getVcAmount();
        this.name = cache.getName();
        this.avatar = cache.getAvatar();
        this.typeId = cache.getType();

    }
}
