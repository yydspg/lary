package cn.lary.core.ratelimiter.aop;

import cn.lary.core.aop.AnnoMethodPoint;
import cn.lary.core.lock.anno.Lock;
import cn.lary.core.lock.aop.LockMethodInterceptor;
import cn.lary.core.ratelimiter.anno.Rate;
import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author paul 2024/4/14
 */

public class RateAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware{
    private final RateInterceptor advice;
    private final Pointcut pointcut = new ComposablePointcut(new AnnoMethodPoint(Rate.class));

    public RateAdvisor(@NonNull RateInterceptor rateInterceptor) {
        this.advice = rateInterceptor;
    }
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
