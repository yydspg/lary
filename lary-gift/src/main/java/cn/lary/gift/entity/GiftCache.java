package cn.lary.gift.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GiftCache {
    /**
     * gift type id
     */
    private int type;

    private String typeName;

    /**
     * virtual currency price
     */
    private Long vcAmount;

    /**
     * real pay price CNY
     */
    private BigDecimal amount;

    private String name;

    /**
     * gift logo
     */
    private String avatar;
}
