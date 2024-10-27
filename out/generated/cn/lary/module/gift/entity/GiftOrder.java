package cn.lary.module.gift.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
@TableName("gift_order")
public class GiftOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 记录id
     */
    private String orderId;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 主播uid
     */
    private String anchorUid;

    /**
     * 购买通道id
     */
    private String buyChannelId;

    /**
     * wk 弹幕流id
     */
    private String danmakuId;

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
    private String streamId;

    /**
     * 是否直接向主播支付,不通过wallet或者余额不足
     */
    private Boolean isToAnchor;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Short status;

    /**
     * 订单编号
     */
    private String sn;

    /**
     * 完成时间
     */
    private LocalDateTime completeAt;

    /**
     * 花费
     */
    private Integer cost;

    /**
     * 支付来源
     */
    private Short clientType;

    /**
     * 礼物uid
     */
    private String giftId;

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
