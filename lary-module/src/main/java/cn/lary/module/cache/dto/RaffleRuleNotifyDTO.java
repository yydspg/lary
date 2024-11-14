package cn.lary.module.cache.dto;

import lombok.Data;

@Data
public class RaffleRuleNotifyDTO {
    private int shard;
    /**
     * 抽奖事件id
     */
    private long eid;

    private int limit;
}
