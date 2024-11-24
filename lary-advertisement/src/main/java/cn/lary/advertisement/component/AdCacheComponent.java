package cn.lary.advertisement.component;

import cn.lary.advertisement.dto.ADCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdCacheComponent {
    private final String LARY_AD_CACHE = "lary:ad:cache";

    private final  Cache<Long, ADCache> adCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .softValues()
            .build();

    private final RedissonClient redisson;

    public static void main(String[] args) {

    }
    public void test(){
        RList<ADCache> list = redisson.<ADCache>getList(LARY_AD_CACHE + 100);
    }
//    private CacheLoader<Long,ADCache> loader() {
//        return new CacheLoader<Long, ADCache>() {
//            @Override
//            public @Nullable ADCache load(Long key) throws Exception {
//                return redisson.<ADCache>getBucket(LARY_AD_CACHE + key).get();
//            }
//        };
//    }

}
