package cn.lary.module.raffle.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RaffleEventCache {
    /**
     * 直播id
     */
    private Long streamId;

    private long raffleId;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 抽奖类型
     */
    private Integer category;

    /**
     * 抽奖条件参数
     */
    private String param;

    /**
     * 抽奖人数
     */
    private Integer num;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    /**
     * 抽奖标题
     */
    private String title;

    /**
     * 抽奖物品描述
     */
    private String content;

    /**
     * 物品数目
     */
    private Integer itemNum;
    /**
     * 持续时间
     */
    private Long duration;

    private BigDecimal totalAmount;

    private BigDecimal amount;

    private Boolean fan;

    private Integer level;

}
