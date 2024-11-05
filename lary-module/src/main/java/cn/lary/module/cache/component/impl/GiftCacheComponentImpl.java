package cn.lary.module.cache.component.impl;

import cn.lary.module.cache.component.GiftCacheComponent;
import cn.lary.module.gift.entity.GiftCache;
import cn.lary.module.gift.entity.GiftCollectionCache;
import org.springframework.stereotype.Component;

@Component
public class GiftCacheComponentImpl implements GiftCacheComponent {

    @Override
    public void setGift(long giftId, GiftCache cache) {

    }

    @Override
    public void dropGift(long giftId) {

    }

    @Override
    public GiftCache getGift(long giftId) {
        return null;
    }

    @Override
    public void setCollection(int typeId, GiftCollectionCache cache) {

    }

    @Override
    public void dropCollection(int typeId) {

    }

    @Override
    public GiftCollectionCache getCollection(int typeId) {
        return null;
    }
}
