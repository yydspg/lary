package cn.lary.module.cache.component;

import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;

public interface RedpacketCacheComponent {

    String LARY_REDPACKET = "lary:redpacket:";
    String LARY_REDPACKET_RULE = "lary:redpacket:rule";

    @Cached(name = LARY_REDPACKET,key = "#uid",cacheType = CacheType.BOTH)
    RedpacketEventCache getRedpacket(long uid);

    @CacheUpdate(name = LARY_REDPACKET,key = "#uid",value = "#cache")
    void setRedpacket(long uid, RedpacketEventCache cache);

    @CacheInvalidate(name = LARY_REDPACKET,key = "#uid")
    void dropRedpacket(long uid);

    @CacheUpdate(name = LARY_REDPACKET_RULE,key = "#uid",value = "#cache")
    void setRule(long uid, RedpacketRuleCache cache);

    @Cached(name = LARY_REDPACKET_RULE,key = "#uid")
    RedpacketRuleCache getRule(long uid);

    @CacheInvalidate(name = LARY_REDPACKET_RULE,key = "#uid")
    void dropRule(long uid);
}
