package cn.lary.module.cache.component;

import cn.lary.module.gift.entity.GiftCache;
import cn.lary.module.gift.entity.GiftCollectionCache;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import org.springframework.stereotype.Component;


@Component
public class GiftCacheComponent {

    String LARY_GIFT = "lary:gift:";
    String LARY_GIFT_COLLECTION = "lary:gift:collection:";

    public void setGift(long giftId, GiftCache cache){
        
    }

    public void dropGift(long giftId){
        
    }

    public GiftCache getGift(long giftId){
        return null;
    }

    public void setCollection(int typeId, GiftCollectionCache cache){
        
    }

    public void dropCollection(int typeId){
        
    }

    public GiftCollectionCache getCollection(int typeId){
        return null;
    }
}
