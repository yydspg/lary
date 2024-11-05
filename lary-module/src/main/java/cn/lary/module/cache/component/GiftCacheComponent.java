package cn.lary.module.cache.component;

import cn.lary.module.gift.entity.GiftCache;
import cn.lary.module.gift.entity.GiftCollectionCache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import org.springframework.stereotype.Component;

public interface GiftCacheComponent {

    String LARY_GIFT = "lary:gift:";
    String LARY_GIFT_COLLECTION = "lary:gift:collection:";

    @CacheUpdate(name = LARY_GIFT,key = "#giftId",value = "#cache")
    void setGift(long giftId, GiftCache cache);

    @CacheInvalidate(name = LARY_GIFT,key = "#giftId")
    void dropGift(long giftId);

    @Cached(name = LARY_GIFT,key = "#giftId",cacheType = CacheType.BOTH)
    GiftCache getGift(long giftId);

    @CacheUpdate(name = LARY_GIFT_COLLECTION,key = "#typeId",value = "#cache")
    void setCollection(int typeId, GiftCollectionCache cache);

    @CacheInvalidate(name = LARY_GIFT_COLLECTION,key = "#typeId")
    void dropCollection(int typeId);

    @Cached(name = LARY_GIFT_COLLECTION,key = "#typeId")
    GiftCollectionCache getCollection(int typeId);
}
