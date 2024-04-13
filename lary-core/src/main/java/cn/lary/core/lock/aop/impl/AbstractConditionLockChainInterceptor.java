package cn.lary.core.lock.aop.impl;

import cn.lary.core.lock.MethodExpressEvaluator;
import cn.lary.core.lock.anno.Lock;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author paul 2024/4/13
 */
// support multiple Lock annotation,When executed, lock and unlock operations will follow the sequence of {@ link Lock} annotations
public abstract class AbstractConditionLockChainInterceptor extends AbstractConditionalLockInterceptor {

    protected AbstractConditionLockChainInterceptor(MethodExpressEvaluator methodExpressEvaluator){
        super(methodExpressEvaluator);
    }
    @Nullable
    @Override
    protected LockOps resolveLockOps(MethodInvocation invocation) {
        Set<LockOps> ops = AnnotatedElementUtils.findMergedRepeatableAnnotations(invocation.getMethod(), Lock.class).stream()
                .map(this::createLockOps)
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (CollectionUtils.isEmpty(ops)) {
            return null;
        }
        if (ops.size() == 1) {
            return ops.iterator().next();
        }
        return getMultiLock(ops);
    }
    // TODO 2024/4/13 : 此处的处理是不担心 GC 吗
    protected LockOps getMultiLock(Set<LockOps> ops) {
        Iterator<LockOps> it = ops.iterator();
        LockOpsChain head = new LockOpsChain(it.next());
        LockOpsChain tem = head;
        while(it.hasNext()) {
            tem.next = new LockOpsChain(it.next());
            tem = tem.next;
        }
        return head;
    }
    // TODO 2024/4/13 : 不清楚为何要如此设计链路,
    protected static class LockOpsChain extends AbstractLockDelegate {
        @Nullable
        private LockOpsChain next;
        public LockOpsChain(LockOps delegate) {
            super(delegate);
        }
        @Override
        public MethodInvocation attach(MethodInvocation invocation) {
            return (null == next) ?
                    delegate.attach(invocation) :
                    delegate.attach(next.attach(invocation));
        }
    }
}
