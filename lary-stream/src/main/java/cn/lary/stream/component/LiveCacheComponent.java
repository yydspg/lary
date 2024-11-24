package cn.lary.stream.component;

import cn.lary.stream.dto.LiveCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveCacheComponent {

    private final  String LARY_LIVE = "lary:live:";
    private final LoadingCache<Long,LiveCache> liveCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(loader());

    private final RedissonClient redisson;

    public LiveCache getLive(long sid) {
        return liveCache.get(sid);
    }

    public void setLive(long sid, LiveCache cache){
        liveCache.put(sid, cache);
    }

    public void dropLive(long sid){
        liveCache.invalidate(sid);
    }

    public void incrWatchNum(long rid) {

    }

    public void setStream(long sid){

    }

    private CacheLoader<Long,LiveCache>  loader() {
        return new CacheLoader<Long, LiveCache>() {

            @Override
            public @Nullable LiveCache load(Long key) throws Exception {
                return redisson.<LiveCache>getBucket(LARY_LIVE + key).get();
            }
        };
    }
}
