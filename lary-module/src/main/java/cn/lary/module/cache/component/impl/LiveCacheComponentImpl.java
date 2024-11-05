package cn.lary.module.cache.component.impl;

import cn.lary.module.cache.component.LiveCacheComponent;
import cn.lary.module.cache.dto.LiveCache;
import org.springframework.stereotype.Component;

@Component
public class LiveCacheComponentImpl implements LiveCacheComponent {

    @Override
    public LiveCache getLive(long uid) {
        return null;
    }

    @Override
    public void setLive(long uid, LiveCache cache) {

    }

    @Override
    public void dropLive(long uid) {

    }
}
