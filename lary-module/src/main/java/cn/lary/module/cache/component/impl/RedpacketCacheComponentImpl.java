package cn.lary.module.cache.component.impl;

import cn.lary.module.cache.component.RedpacketCacheComponent;
import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;
import org.springframework.stereotype.Component;

@Component
public class RedpacketCacheComponentImpl implements RedpacketCacheComponent {

    @Override
    public RedpacketEventCache getRedpacket(long uid) {
        return null;
    }

    @Override
    public void setRedpacket(long uid, RedpacketEventCache cache) {

    }

    @Override
    public void dropRedpacket(long uid) {

    }

    @Override
    public void setRule(long uid, RedpacketRuleCache cache) {

    }

    @Override
    public RedpacketRuleCache getRule(long uid) {
        return null;
    }

    @Override
    public void dropRule(long uid) {

    }
}
