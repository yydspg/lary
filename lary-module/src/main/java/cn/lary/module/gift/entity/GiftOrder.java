package cn.lary.module.gift.entity;

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
@TableName("gift_order")
public class GiftOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 订单id
     */
    private Long oid;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 主播uid
     */
    private Long aid;

    /**
     * wk 弹幕流id
     */
    private Long cid;
    /**
     * 直播流id
     */
    private Long sid;
    /**
     * 礼物uid
     */
    private Integer gid;


    /**
     * 礼物购买数量
     */
    private Integer num;

    /**
     * 异步通知地址
     */
    private String notify;

    /**
     * 是否同步用户数据成功
     */
    private Integer sync;

    /**
     * 上游异常原因
     */
    private String reason;



    /**
     * 是否直接向主播支付,不通过wallet或者余额不足
     */
    private Boolean direct;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Integer status;

    /**
     * 外部订单编号
     */
    private String sn;

    /**
     * 完成时间
     */
    private Long completeAt;

    /**
     * 花费
     */
    private BigDecimal amount;

    /**
     * 支付来源
     */
    private Integer client;



    private Long creatAt;

    private Long updateAt;
    


}
