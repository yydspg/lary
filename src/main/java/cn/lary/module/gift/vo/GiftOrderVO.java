package cn.lary.module.gift.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
    public class GiftOrderVO {

    /**
     * 主播uid
     */
    private Integer anchorUid;

    /**
     * 直播流id
     */
    private Integer streamId;

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
     * 礼物name
     */
    private String giftName;

    /**
     * 礼物购买数量
     */
    private Integer giftNum;
}
