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
     * 审核信息
     */
    private String authMessage;

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
     * 计量单位
     */
    private String goodsUnit;

    /**
     * 商品视频
     */
    private String goodsVideo;

    /**
     * 商品好评率
     */
    private BigDecimal grade;

    /**
     * 商品详情
     */
    private String intro;

    /**
     * 审核状态
     */
    private Integer authStatus;

    /**
     * 上架状态
     */
    private Integer marketStatus;

    /**
     * 商品移动端详情
     */
    private String mobileIntro;

    /**
     * 原图路径
     */
    private String original;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer quantity;

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
     * 卖点
     */
    private String sellingPoint;

    /**
     * 店铺分类
     */
    private String shopCategoryPath;

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
     * 商品编号
     */
    private String sn;

    /**
     * 运费模板ID
     */
    private String templateId;

    /**
     * 下架原因
     */
    private String underMessage;

    /**
     * 店铺分类路径
     */
    private String storeCategoryPath;

    private String goodsType;

    private Boolean isDelete;

    private String createBy;

    private String updateBy;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

      @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
