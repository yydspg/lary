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
    /**
     * flow id
     */
    private Long fid;
    /**
     * uid
     */
    private Long uid;

    private Long toUid;

    /**
     * 直播流id
     */
    private Long sid;

    /**
     * 单笔收入
     */
    private BigDecimal amount;

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
    private Integer gid;

    private Integer num;

    private Long createAt;


    public AnchorFLow() {}

    public AnchorFLow(GiftOrder order){

        setUid(order.getAid());
        setToUid(order.getUid());
        setGid(order.getGid());
        setAmount(order.getAmount());
        setSid(order.getSid());
        setNum(order.getNum());
        setClient(order.getClient());
        setCompleteAt(LocalDateTime.now());
    }
}
