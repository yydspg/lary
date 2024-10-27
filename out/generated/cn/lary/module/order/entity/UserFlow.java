package cn.lary.module.order.entity;

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
@TableName("user_flow")
public class UserFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 库索引
     */
    private Integer idxDb;

    /**
     * 表索引
     */
    private Integer idxTable;

    /**
     * format : 240920-xxxxxxxxxxx
     */
    private Long orderId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺头像地址
     */
    private String storeAvatar;

    /**
     * 货物id
     */
    private Long goodsId;

    /**
     * sku
     */
    private Long sku;

    /**
     * 商品图片
     */
    private String goodsAvatar;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 实际支付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单状态
     */
    private Integer status;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
}
