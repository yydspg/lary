package cn.lary.api.raffle.vo;

import cn.lary.api.raffle.dto.RaffleEventCache;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RaffleEventVO {
    /**
     * 直播id
     */
    private long sid;


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

    public RaffleEventVO() {}

    public RaffleEventVO(RaffleEventCache event) {
        this.sid = event.getSid();
        this.uid = event.getUid();
        this.category = event.getCategory();
        this.param = event.getParam();
        this.num = event.getNum();
        this.sync = event.getSync();
        this.title = event.getTitle();
        this.content = event.getContent();
        this.itemNum = event.getItemNum();
        this.duration = event.getDuration();
        this.totalAmount = event.getTotalAmount();
        this.amount = event.getAmount();
        this.fan = event.getFan();
        this.level = event.getLevel();
    }
}
