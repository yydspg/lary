package cn.lary.core.ratelimiter.aop;


import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.ploy.LimitFactory;
import cn.lary.core.ratelimiter.ploy.RatePloy;
import cn.lary.core.ratelimiter.ploy.RatePloyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author paul 2024/4/15
 */

public class RateInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> clz = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        if (!clz.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Rate rate = AnnotatedElementUtils.getMergedAnnotation(invocation.getMethod(), Rate.class);
        RatePloy ratePloy = RatePloyFactory.getRatePloy(rate.ploy());
        return ratePloy.execute(LimitFactory.get(":vcsaca", rate), invocation::proceed);
    }
}
