package cn.lary.module.goods.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsSkuCacheDTO {
    /**
     * 商品id
     */
    private long goodsId;

    /**
     * sku
     */
    private long sku;

    /**
     * 成本价格
     */
    private BigDecimal cost;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 是否为推荐商品
     */
    private Boolean isRecommend;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品库存
     */
    private int quantity;
}
