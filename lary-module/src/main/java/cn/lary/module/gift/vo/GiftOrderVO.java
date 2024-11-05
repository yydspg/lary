package cn.lary.module.gift.vo;

import cn.lary.module.gift.entity.GiftOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GiftOrderVO {

    /**
     * 主播uid
     */
    private long anchorUid;

    /**
     * 直播流id
     */
    private long streamId;

    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private int status;

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
    private BigDecimal amount;

    /**
     * 支付来源
     */
    private int payWay;


    /**
     * 礼物id
     */
    private int giftId;

    /**
     * 礼物购买数量
     */
    private int giftNum;

    public GiftOrderVO() {}

    public GiftOrderVO(GiftOrder order) {
        this.anchorUid = order.getAnchorUid();
        this.streamId = order.getStreamId();
        this.status = order.getStatus();
        this.sn = order.getSn();
        this.completeAt = order.getCompleteAt();
        this.amount = order.getAmount();
        this.giftId = order.getGiftId();
    }
}
