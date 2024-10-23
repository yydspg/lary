package cn.lary.module.user.kit;

import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenPairKit {

    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;


}
