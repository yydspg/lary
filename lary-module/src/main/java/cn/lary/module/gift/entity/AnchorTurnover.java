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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("anchor_turnover")
public class AnchorTurnover implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer anchorId;

    private Integer buyUid;

    /**
     * 直播流id
     */
    private Integer streamId;

    /**
     * 单笔收入
     */
    private Long income;

    /**
     * 收入来源web,app
     */
    private Integer clientType;

    /**
     * 交易完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 礼物uid
     */
    private Integer giftId;

    private Integer giftNum;

    /**
     * 是否是粉丝
     */
    private Boolean isFan;

    private Boolean isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;

    public AnchorTurnover of(GiftOrder order){

        setAnchorId(order.getAnchorUid());
        setBuyUid(order.getUid());
        setGiftId(order.getGiftId());
        setIncome(order.getCost());
        setStreamId(order.getStreamId());
        setGiftNum(order.getGiftNum());
        setClientType(order.getClientType());
        setCompleteTime(LocalDateTime.now());
        return this;
    }
}
