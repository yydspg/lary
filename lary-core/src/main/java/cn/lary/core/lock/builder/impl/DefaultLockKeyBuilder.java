package cn.lary.core.lock.builder.impl;

import cn.lary.core.lock.MethodExpressEvaluator;
import cn.lary.core.lock.anno.Lock;
import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.config.LockProp;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author paul 2024/4/13
 */

@RequiredArgsConstructor
public class DefaultLockKeyBuilder implements LockKeyBuilder {
    private final MethodExpressEvaluator methodExpressEvaluator;

    private final LockProp lockProp;
    // TODO 2024/4/15 : 修改 build 逻辑,增加 本地缓存,防止多次生成key
    @Override
    public String build(MethodInvocation invocation, Lock lock) {
        StringBuilder sb = new StringBuilder(lockProp.getLockKeyPrefix());
        sb.append(":");
        if(StringUtils.hasText(lock.name())) sb.append(lock.name());
        else sb.append(invocation.getMethod().getDeclaringClass()).append(invocation.getMethod().getName());
        sb.append(this.getSpelKey(lock.keys(), invocation));
        return sb.toString();
    }
    // TODO 2024/4/13 : 未实现
    protected String getSpelKey(String[] definitionKeys,MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        return null;
    }
}
