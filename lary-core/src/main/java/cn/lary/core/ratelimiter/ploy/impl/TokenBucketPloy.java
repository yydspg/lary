package cn.lary.core.ratelimiter.ploy.impl;

import cn.lary.core.cache.impl.RedisCache;
import cn.lary.core.constant.RateCS;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.impl.TokenBucket;
import cn.lary.core.ratelimiter.ploy.RatePloy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @author paul 2024/4/15
 */
@Component
@RequiredArgsConstructor
public class TokenBucketPloy extends RatePloy<TokenBucket> {

    private final StringRedisTemplate str;
    private final DefaultRedisScript<Boolean> tokenBucKtLimitLuaScript;
    @Override
    public String getName() {
        return RateCS.Ploy.TOKEN_BUCKET;
    }

    @Override
    public Boolean isLimit(TokenBucket limit) {
        return str.execute(tokenBucKtLimitLuaScript, Collections.singletonList(limit.getKey()),limit.getKey(),"sss","");
    }

}
