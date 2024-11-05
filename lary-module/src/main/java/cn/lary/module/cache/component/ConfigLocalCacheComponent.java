package cn.lary.module.cache.component;

import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;

public interface ConfigLocalCacheComponent {

    String  LARY_CONFIG = "lary:config:";

    @CacheUpdate(name = LARY_CONFIG,key = "register",value = "#data")
    void  changeRegister(boolean data);

    @Cached(name = LARY_CONFIG,key = "register")
    boolean getRegisterSwitch();


}
