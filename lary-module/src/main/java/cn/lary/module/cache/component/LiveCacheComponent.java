package cn.lary.module.cache.component;

import cn.lary.module.cache.dto.LiveCache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;

public interface LiveCacheComponent {

    String LARY_LIVE = "lary:live:";

    @Cached(name = LARY_LIVE,key = "#uid",cacheType = CacheType.BOTH)
    LiveCache getLive(long uid);

    @CacheUpdate(name = LARY_LIVE,key = "#uid",value = "#cache")
    void setLive(long uid, LiveCache cache);

    @CacheInvalidate(name = LARY_LIVE,key = "#uid")
    void dropLive(long uid);
}
