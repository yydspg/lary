package cn.lary.module.raffle.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RaffleEventCache {

    /**
     * 直播id
     */
    private long sid;

    /**
     * 事件id
     */
    private long eid;

    /**
     * 直播间id
     */
    private long rid;

    /**
     * 用户id
     */
    private long uid;

    /**
     * 抽奖类型
     */
    private int category;

    /**
     * 抽奖条件参数
     */
    private String param;

    /**
     * 抽奖人数
     */
    private int num;

    /**
     * 同步状态
     */
    private int sync;

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
    private int itemNum;
    /**
     * 持续时间
     */
    private long duration;

    private BigDecimal totalAmount;

    private BigDecimal amount;

    private Boolean fan;

    private int level;

}
