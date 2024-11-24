package cn.lary.module.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@TableName("order_complaint")
public class OrderComplaint implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * uid
     */
    private Long uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 申诉商家内容
     */
    private String appealContent;

    /**
     * 申诉商家上传的图片
     */
    private String appealImages;

    /**
     * 申诉商家时间
     */
    private LocalDateTime appealAt;

    /**
     * 仲裁结果
     */
    private String arbitrationResult;

    /**
     * 交易投诉状态
     */
    private Integer complainStatus;

    /**
     * 投诉主题
     */
    private String complainTopic;

    /**
     * 收货地址
     */
    private String consigneeAddress;

    /**
     * 收货人手机
     */
    private String consigneeMobile;

    /**
     * 收货人
     */
    private String consigneeName;

    /**
     * 投诉内容
     */
    private String content;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品价格
     */
    private BigDecimal goodsAmount;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 投诉凭证图片
     */
    private String images;

    /**
     * 物流单号
     */
    private String logisticsNo;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 订单编号
     */
    private Long orderSn;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 店铺ID
     */
    private Integer storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * sku
     */
    private String skuId;

 

      

    
}
