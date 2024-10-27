package cn.lary.module.order.entity;

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
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * format : 240920-xxxxxxxxxxx
     */
    private Long id;

    /**
     * uid
     */
    private Long uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 红包id
     */
    private Long redpacketId;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 商品价格
     */
    private BigDecimal goodsAmount;

    /**
     * 商品总金额
     */
    private BigDecimal goodsTotalAmount;

    /**
     * 店铺优惠的金额
     */
    private BigDecimal discountStoreAmount;

    /**
     * 官方优惠的金额
     */
    private BigDecimal discountLaryAmount;

    /**
     * 红包优惠的金额
     */
    private BigDecimal discountRedpacketAmount;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal payAmount;

    /**
     * 详细地址
     */
    private String consigneeDetail;

    /**
     * 收件人手机
     */
    private String consigneePhone;

    /**
     * 收件人姓名
     */
    private String consigneeName;

    /**
     * 详细地址
     */
    private String shipperDetail;

    /**
     * 收件人手机
     */
    private String shipperPhone;

    /**
     * 收件人姓名
     */
    private String shipperName;

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
     * 物流公司CODE
     */
    private Integer logisticsId;

    /**
     * 物流公司名称
     */
    private String logisticsName;

    /**
     * 发货单号
     */
    private String logisticsOrderNo;

    /**
     * 支付方式返回的交易号
     */
    private String payOrderNo;

    /**
     * 付款状态
     */
    private Integer payStatus;

    /**
     * 货运状态
     */
    private Integer deliverStatus;

    /**
     * 支付方式
     */
    private Integer payWay;

    /**
     * 买家订单备注
     */
    private String remark;

    /**
     * 商家订单备注
     */
    private String sellerRemark;

    /**
     * 店铺ID
     */
    private Integer storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺头像地址
     */
    private String storeAvatar;

    /**
     * 是否需要发票
     */
    private Boolean isNeedReceipt;

    /**
     * 提货码
     */
    private String deliverCode;

    /**
     * 订单是否支持原路退回
     */
    private Boolean isCanReturn;

    /**
     * 订单取消原因
     */
    private String cancelReason;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * 是否为其他订单下的订单，如果是则为依赖订单的sn，否则为空
     */
    private String parentId;

    /**
     * 订单状态
     */
    private Integer status;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    private LocalDateTime paymentAt;

    private LocalDateTime deliverAt;

    private LocalDateTime completeAt;

      @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;

    private Boolean isDelete;
}
