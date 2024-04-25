package cn.lary.core.ratelimiter.builder;

import cn.lary.core.ratelimiter.anno.Rate;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author paul 2024/4/25
 */

public interface RateKeyBuilder {
    String build(MethodInvocation invocation, Rate rate);
}
