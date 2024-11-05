package cn.lary.module.goods.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("goods_sku")
public class GoodsSku implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * sku
     */
    private Long sku;

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
    private Integer quantity;

    /**
     * 促销点:商家自定义
     */
    private String point;

    private Boolean isDelete;

    private String createBy;

    private String updateBy;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

      @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
