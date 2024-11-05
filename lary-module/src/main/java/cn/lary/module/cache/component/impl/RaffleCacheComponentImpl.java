package cn.lary.module.cache.component.impl;

import cn.lary.module.cache.component.RaffleCacheComponent;
import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.raffle.entity.RaffleRuleCache;
import org.springframework.stereotype.Component;

@Component
public class RaffleCacheComponentImpl implements RaffleCacheComponent {

    @Override
    public RaffleEventCache getRaffle(long uid) {
        return null;
    }

    @Override
    public void setRaffle(long uid, RaffleEventCache dto) {

    }

    @Override
    public void dropRaffle(long uid) {

    }

    @Override
    public void setRule(long uid, RaffleRuleCache cache) {

    }

    @Override
    public RaffleRuleCache getRule(long uid) {
        return null;
    }

    @Override
    public void dropRule(long uid) {

    }
}
