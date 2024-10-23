package cn.lary.module.gift.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiftVO {
    /**
     * gift type id
     */
    private int typeId;

    private String typeName;

    /**
     * virtual currency price
     */
    @JsonProperty("vc_price")
    private long vcPrice;

    /**
     * real pay price CNY
     */
    @JsonProperty("price")
    private long price;

    private String name;

    /**
     * gift logo
     */
    private String avatar;
}
