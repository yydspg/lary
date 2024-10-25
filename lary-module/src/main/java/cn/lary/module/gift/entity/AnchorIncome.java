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
public class AnchorIncome implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer uid;

    private Integer toUid;

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
    private Boolean isFan;

    private Boolean isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;

    public AnchorIncome of(GiftOrder order){

        setUid(order.getAnchorUid());
        setToUid(order.getUid());
        setGiftId(order.getGiftId());
        setIncome(order.getCost());
        setStreamId(order.getStreamId());
        setGiftNum(order.getGiftNum());
        setClient(order.getClient());
        setIsFan(order.getIsFan());
        setCompleteAt(LocalDateTime.now());
        return this;
    }
}
