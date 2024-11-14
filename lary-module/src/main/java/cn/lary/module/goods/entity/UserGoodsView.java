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
@TableName("user_goods_view")
public class UserGoodsView implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 库索引
     */
    private Integer idxDb;

    /**
     * 表索引
     */
    private Integer idxTable;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 店铺ID
     */
    private Long storeId;

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
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 大图路径
     */
    private String bigAvatar;

 
}
