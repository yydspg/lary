package cn.lary.module.cache.dto;

import lombok.Data;

@Data
public class RaffleRuleNotifyDTO {
    private int shard;

    private long uid;

    private int limit;
}
