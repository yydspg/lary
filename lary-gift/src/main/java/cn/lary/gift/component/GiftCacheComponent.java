package cn.lary.gift.component;
import cn.lary.api.gift.entity.GiftCache;
import cn.lary.api.gift.entity.GiftCollectionCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiftCacheComponent {

    String LARY_GIFT = "lary:gift:";
    String LARY_GIFT_COLLECTION = "lary:gift:collection:";

    private final RedissonClient redisson;
    private final Cache<Long, GiftCache> giftCache = Caffeine.newBuilder().build();
    private final Cache<Integer, GiftCollectionCache> giftCollectionCache = Caffeine.newBuilder().build();

    public void setGift(long giftId, GiftCache cache){
        redisson.getBucket(LARY_GIFT+giftId).set(cache);
    }

    public void dropGift(long giftId){
        redisson.getBucket(LARY_GIFT+giftId).delete();
    }

    public void setLocalGift(long giftId, GiftCache cache){
        giftCache.put(giftId,cache);
    }

    public void dropLocalGift(long giftId){
        giftCache.invalidate(giftId);
    }

    public GiftCache getGift(long giftId){
        return giftCache.getIfPresent(giftId);
    }

    public void setLocalCollection(int typeId, GiftCollectionCache cache){
        giftCollectionCache.put(typeId,cache);
    }
    public void setCollection(int typeId, GiftCollectionCache cache){
        redisson.getBucket(LARY_GIFT_COLLECTION+typeId).set(cache);
    }
    public void dropCollection(int typeId){
        redisson.getBucket(LARY_GIFT_COLLECTION+typeId).delete();
    }
    public void dropLocalCollection(int typeId){
        giftCollectionCache.invalidate(typeId);
    }
    public GiftCollectionCache getCollection(int typeId){
        GiftCollectionCache cache = giftCollectionCache.getIfPresent(typeId);
        if(cache != null){
            return cache;
        }
        cache = redisson.<GiftCollectionCache>getBucket(LARY_GIFT_COLLECTION+typeId).get();
        if(cache != null){
            return cache;
        }
        log.error("get collection cache error, typeId:{}",typeId);
        return null;
    }
}
