package cn.lary.module.cache.component;

import cn.lary.module.cache.dto.LiveCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveCacheComponent {

    private final  String LARY_LIVE = "lary:live:";
    private final  Cache<Long,LiveCache> liveCache = Caffeine.newBuilder().build();
    private final RedissonClient redisson;

    public LiveCache getLive(long uid) {
        LiveCache cache = liveCache.getIfPresent(uid);
        if (cache != null) {
            return cache;
        }
        cache = redisson.<LiveCache>getBucket(LARY_LIVE + uid).get();
        if (cache != null) {
            return cache;
        }
        log.error("no live cache found for uid:{}", uid);
        return null;
    }

    public void setLive(long uid, LiveCache cache){
        liveCache.put(uid, cache);
    }

    public void dropLive(long uid){
        liveCache.invalidate(uid);
    }
}
