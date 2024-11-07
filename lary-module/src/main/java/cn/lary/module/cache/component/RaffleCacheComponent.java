package cn.lary.module.cache.component;

import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.raffle.entity.RaffleRuleCache;
import org.springframework.stereotype.Component;

@Component
public class RaffleCacheComponent {

    String LARY_RAFFLE = "lary:raffle:";
    String LARY_RAFFLE_RULE = "lary:raffle:rule";

    public RaffleEventCache getRaffle(long uid){
        return null;
    }
    public void setRaffle(long uid, RaffleEventCache dto){

    }

    public void dropRaffle(long uid){

    }
    public void setRule(long uid, RaffleRuleCache cache){

    }
    public RaffleRuleCache getRule(long uid){
        return null;
    }
    public void dropRule(long uid){

    }


}
