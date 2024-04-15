package cn.lary.core.ratelimiter.model.impl;

import cn.lary.core.exception.rateLimiter.RateException;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.Limit;
import lombok.Data;

/**
 * @author paul 2024/4/15
 */

public class TokenBucket extends Limit{
    public TokenBucket(String K,Rate rate) {
        super(K,rate);
    }
}
