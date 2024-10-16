package cn.lary.module.pay.entity;

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
 * @since 2024-09-13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("payment_log")
public class PaymentLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户uid
     */
    private Integer uid;

    /**
     * 业务订单编号，id
     */
    private String orderId;

    /**
     * 订单类型 1 充值钱包 2 购买礼物,根据业务类型变更
     */
    private Byte orderType;

    /**
     * 第三方支付成功交易号
     */
    private String tradeNo;

    /**
     * 支付方式 1 支付宝 2 微信支付
     */
    private Byte payWay;

    /**
     * 支付状态 1 成功 2 失败
     */
    private Byte payStatus;

    /**
     * 支付金额
     */
    private BigDecimal payCost;

    /**
     * 请求支付状态，1：成功，2：失败
     */
    private Byte postStatus;

    /**
     * 回调状态，1：成功，2：失败
     */
    private Byte returnStatus;

    /**
     * 提交post json 数据
     */
    private String postJson;

    /**
     * 回调post return data json
     */
    private String returnJson;

    /**
     * 第三方返回错误码
     */
    private String resultCode;

    /**
     * 第三方返回的错误记录
     */
    private String errCodeStr;

    private Boolean isDelete;

    

    

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
