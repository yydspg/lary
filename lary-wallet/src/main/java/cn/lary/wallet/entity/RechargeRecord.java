package cn.lary.wallet.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2024-09-13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("recharge_log")
public class RechargeRecord implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 充值id
     */
    private Long cid;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Integer status;

    /**
     * 是否同步，比如wallet是否同步成功
     */
    private Boolean isSync;

    /**
     * 完成时间
     */
    private long completeAt;

    /*
    外部的支付订单号
     */
    private String sn;

    /**
     * 花费
     */
    private BigDecimal amount;

    /**
     * 获得的虚拟货币数目
     */
    private Long starNum;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 上游异常原因
     */
    private String failReason;

    /**
     * 充值事件id
     */
    private Long eid;
    /**
     * 支付来源
     */
    private Integer payway;



}
