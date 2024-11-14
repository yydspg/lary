package cn.lary.module.goods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsDetailsCacheDTO {

    private long goodsId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 购买数量
     */
    private int buyCount;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;


    /**
     * 店铺ID
     */
    private long storeId;

    /**
     * 店铺名称
     */
    private String storeName;
    /**
     * 原图路径
     */
    private String original;

    /**
     * 商品优点标签
     */
    private String tags;

    /**
     * 商品参数
     */
    private String params;

    /**
     * 详情图片
     */
    private String detailImages;

    @JSONField(format="sku")
    private List<GoodsSkuCacheDTO> sku;

}
