package cn.lary.core.ratelimiter.ploy;

import cn.lary.core.constant.RateCS;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.Limit;
import cn.lary.core.ratelimiter.model.impl.FixedWindow;
import cn.lary.core.ratelimiter.model.impl.SlidingWindow;
import cn.lary.core.ratelimiter.model.impl.TokenBucket;

/**
 * @author paul 2024/4/15
 */

public class LimitFactory {
    private LimitFactory(){}

    public static  Limit get(String key, Rate rate) {
         return switch (key) {
            case RateCS.Ploy.FIXED_WINDOW -> new FixedWindow(key,rate);
            case RateCS.Ploy.SLIDING_WINDOW -> new SlidingWindow(key,rate);
            default -> new TokenBucket(key,rate);
        };
    }
}
