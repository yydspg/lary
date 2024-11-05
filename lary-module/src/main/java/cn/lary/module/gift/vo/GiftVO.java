package cn.lary.module.gift.vo;

import cn.lary.module.gift.entity.GiftCache;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("vc_amount")
    private long vcAmount;

    /**
     * real pay price CNY
     */
    @JsonProperty("amount")
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
