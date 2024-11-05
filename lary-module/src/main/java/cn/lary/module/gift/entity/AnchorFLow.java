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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("anchor_turnover")
public class AnchorFLow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long uid;

    private Long toUid;

    /**
     * 直播流id
     */
    private Long streamId;

    /**
     * 单笔收入
     */
    private BigDecimal income;

    /**
     * 收入来源web,app
     */
    private Integer client;

    /**
     * 交易完成时间
     */
    private LocalDateTime completeAt;

    /**
     * 礼物uid
     */
    private Integer giftId;

    private Integer giftNum;

    /**
     * 是否是粉丝
     */
    private Integer followStatus;

    private Boolean isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;

    public AnchorFLow() {}

    public AnchorFLow(GiftOrder order){

        setUid(order.getAnchorUid());
        setToUid(order.getUid());
        setGiftId(order.getGiftId());
        setIncome(order.getAmount());
        setStreamId(order.getStreamId());
        setGiftNum(order.getGiftNum());
        setClient(order.getClient());
        setFollowStatus(order.getFollowStatus());
        setCompleteAt(LocalDateTime.now());

    }
}
