package cn.lary.core.lock.aop;

import cn.lary.core.aop.AnnoMethodPoint;
import cn.lary.core.lock.anno.Lock;
import lombok.NonNull;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author paul 2024/4/13
 */

public class LockAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final LockMethodInterceptor advice;
    private final Pointcut pointcut = new ComposablePointcut(new AnnoMethodPoint(Lock.class))
            .union(new AnnoMethodPoint(Lock.List.class));

    public LockAdvisor(@NonNull LockMethodInterceptor lockInterceptor, int order) {
        this.advice = lockInterceptor;
        setOrder(order);
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
