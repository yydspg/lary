package cn.lary.module.user.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacheComponent {

    private final  String LARY_USER = "lary:user:";
    private final Cache<Long, UserCache> userCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    private final RedissonClient redisson;

    public void setData(long uid, UserCache cache) {
        redisson.<UserCache>getBucket(LARY_USER + uid).set(cache);
    }
    public void dropData(long uid) {
        redisson.<UserCache>getBucket(LARY_USER + uid).delete();
    }

    public UserCache getData(long uid) {
        return redisson.<UserCache>getBucket(LARY_USER + uid).get();
    }
    private CacheLoader<Long,UserCache> loader() {
        return new CacheLoader<Long, UserCache>() {

            @Override
            public @Nullable UserCache load(Long key) throws Exception {
                return redisson.<UserCache>getBucket(LARY_USER + key).get();
            }
        };
    }
}
