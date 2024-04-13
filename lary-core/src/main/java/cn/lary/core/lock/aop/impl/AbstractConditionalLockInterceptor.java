package cn.lary.core.lock.aop.impl;

import cn.lary.core.lock.MethodExpressEvaluator;
import cn.lary.core.lock.anno.Lock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

/**
 * @author paul 2024/4/13
 */
// TODO 2024/4/13 : 理解不了啊!!!! 这些类的作用是什么
@RequiredArgsConstructor
public abstract class AbstractConditionalLockInterceptor extends AbstractLockInterceptor{

    private final MethodExpressEvaluator methodExpressEvaluator;

    @Override
    protected LockOps createLockOps(Lock annotation) {
        LockOps delegate = super.createLockOps(annotation);
        String condition = annotation.condition();
        if (!StringUtils.hasText(condition)) {
            return delegate;
        }
        return new ConditionalLockOps(delegate, condition);
    }

    protected final boolean evaluateCondition(String condition, MethodInvocation invocation) {
        return methodExpressEvaluator.getV(
                invocation.getMethod(), invocation.getArguments(), condition, Boolean.class);
    }

    @Getter
    protected class ConditionalLockOps extends AbstractLockDelegate {
        private final String condition;
        public ConditionalLockOps(LockOps delegate, String condition) {
            super(delegate);
            this.condition = condition;
        }
        @Override
        public MethodInvocation attach(MethodInvocation invocation) {
            return evaluateCondition(condition, invocation) ?
                    delegate.attach(invocation) : invocation;
        }
    }
}
