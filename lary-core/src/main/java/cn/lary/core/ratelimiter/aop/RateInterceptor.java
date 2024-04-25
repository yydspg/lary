package cn.lary.core.ratelimiter.aop;


import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.builder.RateKeyBuilder;
import cn.lary.core.ratelimiter.ploy.LimitFactory;
import cn.lary.core.ratelimiter.ploy.RatePloy;
import cn.lary.core.ratelimiter.ploy.RatePloyFactory;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author paul 2024/4/15
 */
@RequiredArgsConstructor
public class RateInterceptor implements MethodInterceptor {

    private final RateKeyBuilder rateKeyBuilder;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> clz = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        if (!clz.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Rate rate = AnnotatedElementUtils.getMergedAnnotation(invocation.getMethod(), Rate.class);
        RatePloy ratePloy = RatePloyFactory.getRatePloy(rate.ploy());

        return ratePloy.execute(LimitFactory.get(rateKeyBuilder.build(invocation,rate), rate), invocation::proceed);
    }
}
