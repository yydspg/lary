package cn.lary.module.raffle.listener;

import cn.lary.module.raffle.entity.RaffleEventCache;
import lombok.Data;

@Data
public class RaffleEventMessage {

    private RaffleEventCache cache;

}
