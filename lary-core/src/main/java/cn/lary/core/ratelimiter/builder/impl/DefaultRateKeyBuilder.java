package cn.lary.core.ratelimiter.builder.impl;

import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.builder.RateKeyBuilder;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * @author paul 2024/4/25
 */
@Slf4j
public class DefaultRateKeyBuilder implements RateKeyBuilder {
    private final HashMap<String,String> keys = new HashMap<>(8);
    @Override
    public String build(MethodInvocation invocation, Rate rate) {
        if(keys.get(invocation.getMethod().toString()) == null){
            log.info("构建限流key:[{}]",invocation);
            StringBuilder sb = new StringBuilder("rate:");
            if(StringUtils.hasText(rate.name())) sb.append(rate.name());
            else sb.append(invocation.getMethod().getDeclaringClass()).append(invocation.getMethod().getName());
            keys.put(invocation.getMethod().toString(),sb.toString());
        }
        return keys.get(invocation.getMethod().toString());
    }
}
