package cn.lary.core.lock.aop.impl;

import cn.lary.core.exception.lock.LockException;
import cn.lary.core.lock.LockInfo;
import cn.lary.core.lock.LockTemplate;
import cn.lary.core.lock.MethodExpressEvaluator;
import cn.lary.core.lock.anno.Lock;
import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.config.LockProp;
import cn.lary.core.lock.lockFailPloy.LockFailPloy;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author paul 2024/4/13
 */

public class LockOpsInterceptor extends AbstractConditionLockChainInterceptor {
    private final LockTemplate lockTemplate;
    protected final LockProp lockProp;

    public LockOpsInterceptor(MethodExpressEvaluator methodBasedExpressionEvaluator, LockTemplate lockTemplate, LockProp lockProp) {
        super(methodBasedExpressionEvaluator);
        this.lockTemplate = lockTemplate;
        this.lockProp = lockProp;
    }
    @NonNull
    @Override
    protected LockOps initDefaultLockOps() {
        LockKeyBuilder lockKeyBuilder = obtainComponent(LockKeyBuilder.class, lockProp.getPrimaryKeyBuilder());
        LockFailPloy lockFailPloy = obtainComponent(LockFailPloy.class, lockProp.getPrimaryFailureStrategy());
        return new LockOpsImpl(null)
                .setLockKeyBuilder(lockKeyBuilder)
                .setLockFailPloy(lockFailPloy);
    }
    private <C> C obtainComponent(Class<C> type, @Nullable Class<? extends C> defaultType) {
        if (null != defaultType) {
            return applicationContext.getBean(defaultType);
        }
        Collection<C> components = applicationContext.getBeansOfType(type).values();
        return components.stream()
                .min(AnnotationAwareOrderComparator.INSTANCE)
                .orElseThrow(() -> new IllegalArgumentException("NoComponentOfType" + type.getName() + "Found"));
    }
    @Override
    protected Object doLock(LockOps lockOps, MethodInvocation invocation) throws Throwable {
        Lock anno = lockOps.getAnno();
        LockInfo lockInfo = null;
        try {
            String lockK = lockOps.getLockKeyBuilder().build(invocation, lockOps.getAnno());
            lockInfo = lockTemplate.lock(lockK, anno.expire(), anno.acquireTimeout(), anno.exec());
            if(lockInfo != null) return invocation.proceed();
            // lock fail,[bug] if inline lockK,and use lockInfo.getLockK() in thi statement,while produce null point exception
            lockOps.getLockFailPloy().onLockFail(lockK, invocation.getMethod(), invocation.getArguments());
            return null;
        } finally {
            doUnlock(lockInfo,anno);
        }
    }
    private void doUnlock(@Nullable LockInfo lockInfo, Lock lock) {
        if (lockInfo == null || lock.autoRelease()) {
            return ;
        }
        //Usually unlocking is successful
        if (!lockTemplate.unLock(lockInfo)) {
            throw new LockException("errorUnLock: lockK:["+ lockInfo.getLockK()+"]lockV:["+lockInfo.getLockV()+"]");
        }
    }

}
