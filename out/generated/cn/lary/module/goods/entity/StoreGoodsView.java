package cn.lary.module.goods.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("store_goods_view")
public class StoreGoodsView implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      private Long id;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 库索引
     */
    private Integer idxDb;

    /**
     * 表索引
     */
    private Integer idxTable;

    /**
     * id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String name;

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
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 小图路径
     */
    private String smallAvatar;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
}
