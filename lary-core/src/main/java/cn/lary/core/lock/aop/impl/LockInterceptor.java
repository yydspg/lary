package cn.lary.core.lock.aop.impl;

import cn.lary.core.exception.lock.LockException;
import cn.lary.core.lock.LockInfo;
import cn.lary.core.lock.LockTemplate;
import cn.lary.core.lock.anno.Lock;
import cn.lary.core.lock.aop.LockMethodInterceptor;
import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.config.LockProp;
import cn.lary.core.lock.lockFailPloy.LockFailPloy;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  distributed lock aop handler
 * @author paul 2024/4/13
 */
@RequiredArgsConstructor
public class LockInterceptor implements InitializingBean, LockMethodInterceptor {

    private final LockTemplate lockTemplate;

    private final LockProp lockProp;

    private final List<LockFailPloy> failPloys;

    private final List<LockKeyBuilder> keyBuilders;
    private LockOps primaryLockOps;
    private final Map<Class<? extends LockKeyBuilder>,LockKeyBuilder> keyBuilderMap = new LinkedHashMap<>();
    private final Map<Class<? extends LockFailPloy>,LockFailPloy> failPloyMap = new LinkedHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> clz = AopProxyUtils.ultimateTargetClass(invocation.getThis());
        if (!clz.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Lock lock = AnnotatedElementUtils.findMergedAnnotation(invocation.getMethod(), Lock.class);
        LockInfo lockInfo = null;

        try {
            LockOps lockOps = this.buildLockOps(lock);
            String lockK = lockOps.lockKeyBuilder.build(invocation,lock);
            lockInfo = lockTemplate.lock(lockK, lock.expire(), lock.acquireTimeout(), lock.exec());
            if(null != lockInfo) return invocation.proceed();
            // lock fail,[bug] if inline lockK,and use lockInfo.getLockK() in thi statement,while produce null point exception
            lockOps.lockFailPloy.onLockFail(lockK,invocation.getMethod(),invocation.getArguments());
            return null;
        } finally {
            doUnlock(lockInfo,lock);
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
    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO 2024/4/13 : keyBuilders,failPloys 是如何初始化的呢?
        keyBuilderMap.putAll(keyBuilders.stream().collect(Collectors.toMap(LockKeyBuilder::getClass,t->t)));
        failPloyMap.putAll(failPloys.stream().collect(Collectors.toMap(LockFailPloy::getClass,t->t)));
        LockKeyBuilder primaryK;
        LockFailPloy primaryF;

        List<LockKeyBuilder> lockKeyBuilderList = keyBuilders.stream().filter(Ordered.class::isInstance).toList();
        if(lockProp.getPrimaryKeyBuilder() != null) {
            primaryK = keyBuilderMap.get(lockProp.getPrimaryKeyBuilder());
        }else if(!lockKeyBuilderList.isEmpty()) {
            this.sortOperation(lockKeyBuilderList);
            primaryK = lockKeyBuilderList.get(0);
        }else {
            primaryK = keyBuilders.get(0);
        }
        List<LockFailPloy> lockFailPloyList = failPloys.stream().filter(Ordered.class::isInstance).toList();
        if (lockProp.getPrimaryFailureStrategy() != null) {
            primaryF = failPloyMap.get(lockProp.getPrimaryFailureStrategy());
        } else if (!lockFailPloyList.isEmpty()) {
            this.sortOperation(lockFailPloyList);
            primaryF = lockFailPloyList.get(0);
        }else{
            primaryF = failPloys.get(0);
        }
        primaryLockOps = LockOps.builder()
                .lockKeyBuilder(primaryK)
                .lockFailPloy(primaryF)
                .build();
    }

    @Builder
    private static class LockOps {
        private LockKeyBuilder lockKeyBuilder;
        private LockFailPloy lockFailPloy;
    }
    private LockOps buildLockOps(Lock lock) {
        LockKeyBuilder resK;
        LockFailPloy resF;
        Class<? extends LockKeyBuilder> tK = lock.keyBuilderStrategy();
        Class<? extends LockFailPloy> tF = lock.failStrategy();
        resK = (tK == null || tK == LockKeyBuilder.class) ?
                primaryLockOps.lockKeyBuilder : keyBuilderMap.get(tK);
        resF = (tF == null || tF == LockFailPloy.class) ?
                primaryLockOps.lockFailPloy : failPloyMap.get(tF);
        return LockOps.builder()
                .lockKeyBuilder(resK)
                .lockFailPloy(resF).build();
    }
    private void sortOperation(List<?> operations){
        if (operations.size()<=1){
            return;
        }
        operations.sort(OrderComparator.INSTANCE);
    }

}
