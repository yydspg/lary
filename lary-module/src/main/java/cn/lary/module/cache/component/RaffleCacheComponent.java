package cn.lary.module.cache.component;

import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.raffle.entity.RaffleRuleCache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;

public interface RaffleCacheComponent {

    String LARY_RAFFLE = "lary:raffle:";
    String LARY_RAFFLE_RULE = "lary:raffle:rule";

    @Cached(name = LARY_RAFFLE,key = "#uid",cacheType = CacheType.REMOTE)
    RaffleEventCache getRaffle(long uid);

    @CacheUpdate(name = LARY_RAFFLE,key = "#uid",value = "#dto")
    void setRaffle(long uid, RaffleEventCache dto);

    @CacheInvalidate(name = LARY_RAFFLE,key = "#uid")
    void dropRaffle(long uid);

    @CacheUpdate(name = LARY_RAFFLE_RULE,key = "#uid",value = "#cache")
    void setRule(long uid, RaffleRuleCache cache);

    @Cached(name = LARY_RAFFLE_RULE,key = "#uid",cacheType = CacheType.LOCAL)
    RaffleRuleCache getRule(long uid);

    @CacheInvalidate(name = LARY_RAFFLE_RULE,key = "#uid")
    void dropRule(long uid);


}
