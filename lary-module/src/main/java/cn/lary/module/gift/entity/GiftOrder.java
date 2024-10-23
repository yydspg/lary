package cn.lary.module.gift.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
     * 用户id
     */
    private Integer uid;

    /**
     * 主播uid
     */
    private Integer anchorUid;

    /**
     * wk 弹幕流id
     */
    private Integer danmakuId;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 是否同步用户数据成功
     */
    private Boolean isSync;

    /**
     * 上游异常原因
     */
    private String failReason;

    /**
     * 直播流id
     */
    private Integer streamId;

    /**
     * 是否直接向主播支付,不通过wallet或者余额不足
     */
    private Boolean isToAnchor;

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
    private LocalDateTime completeAt;

    /**
     * 花费
     */
    private Long cost;

    /**
     * 支付来源
     */
    private Integer clientType;

    /**
     * 礼物uid
     */
    private Integer giftId;

    /**
     * 礼物name
     */
    private String giftName;

    /**
     * 礼物购买数量
     */
    private Integer giftNum;

    private Boolean isDelete;



    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
