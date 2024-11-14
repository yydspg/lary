package cn.lary.module.goods.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-10-28
 */
@Getter
@Setter
@Accessors(chain = true)
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      private Long id;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 购买数量
     */
    private Integer buyCount;

    /**
     * 分类路径
     */
    private String categoryPath;

    /**
     * 评论数量
     */
    private Integer commentNum;

    /**
     * 成本价格
     */
    private BigDecimal cost;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品分类
     */
    private Integer category;

    /**
     * 计量单位,克,个,米.etc
     */
    private String unit;

    /**
     * 商品视频
     */
    private String video;

    /**
     * 商品好评率
     */
    private BigDecimal grade;

    /**
     * 商品详情
     */
    private String intro;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品库存
     */
    private Integer quantity;

    /**
     * 审核状态
     */
    private Integer authStatus;

    /**
     * 审核信息
     */
    private String authMessage;

    /**
     * 上架状态
     */
    private Integer marketStatus;

    /**
     * 商品移动端详情
     */
    private String mobileIntro;

    /**
     * 是否为推荐商品
     */
    private Boolean isRecommend;

    /**
     * 销售模式
     */
    private String salesModel;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺分类
     */
    private String storeCategory;

    /**
     * 店铺分类路径
     */
    private String storeCategoryPath;

    /**
     * 卖点
     */
    private String sellingPoint;

    /**
     * 小图路径
     */
    private String smallAvatar;

    /**
     * 大图路径
     */
    private String bigAvatar;

    /**
     * 缩略图路径
     */
    private String thumbnail;

    /**
     * 原图路径
     */
    private String original;

    /**
     * 运费模板ID
     */
    private String templateId;

    /**
     * 下架原因
     */
    private String underMessage;
    /**
     * 商品参数
     */
    private String params;

    /**
     * 详情图片
     */
    private String detailImages;
    /**
     * 商品优点标签
     */
    private String tags;

    

    private String createBy;

    private String updateBy;

 

      
}
