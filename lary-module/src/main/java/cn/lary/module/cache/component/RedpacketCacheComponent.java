package cn.lary.module.cache.component;

import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;

public class RedpacketCacheComponent {

    String LARY_REDPACKET = "lary:redpacket:";
    String LARY_REDPACKET_RULE = "lary:redpacket:rule";


    public RedpacketEventCache getRedpacket(long uid){
        return null;
    }

    public void setRedpacket(long uid, RedpacketEventCache cache){

    }

    public void dropRedpacket(long uid){

    }

    public void setRule(long uid, RedpacketRuleCache cache){

    }

    public RedpacketRuleCache getRule(long uid){
       return null;
    }

    public void dropRule(long uid){

    }
}
