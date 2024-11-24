package cn.lary.module.common.cache.impl;

import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.config.RedisBusinessConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KVBuilderImpl implements KVBuilder {

    private final RedisBusinessConfig redisBusinessConfig;


    @Override
    public String userLoginTokenK(String token) {
        return redisBusinessConfig.getTokenCachePrefix() + token;
    }

    @Override
    public String userLoginTokenV(long uid, String username, int role) {
        return uid+"@"+username+"@"+role;
    }


    @Override
    public String raffleListK(long uid) {
        return redisBusinessConfig.getRaffleListPrefix() +uid;
    }

    @Override
    public String redPacketUidMapK(long uid) {
        return redisBusinessConfig.getRedPacketUidPrefix()+uid;
    }

    @Override
    public String redPacketDataListK(long uid) {
        return redisBusinessConfig.getRedPacketDataPrefix()+uid;
    }


}
