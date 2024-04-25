package cn.lary.core.ratelimiter.ploy.impl;

import cn.lary.core.constant.RateCS;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.impl.SlidingWindow;
import cn.lary.core.ratelimiter.ploy.RatePloy;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * @author paul 2024/4/15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlidingWindowPloy extends RatePloy<SlidingWindow> {

    private final StringRedisTemplate srt;

    @Qualifier(value = "slidingWindowLuaScript")
    private final DefaultRedisScript<Boolean> slidingWinLimitScript;
    @Override
    public String getName() {
        return RateCS.Ploy.SLIDING_WINDOW;
    }

    @Override
    public Boolean isLimit(SlidingWindow limit) {
        log.info("执行限流:[{}]",limit.getKey());
        return srt.execute(slidingWinLimitScript, Collections.singletonList(limit.getKey()), limit.getCount(), limit.getSize(), "scscs");
    }

}
