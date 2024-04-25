package cn.lary.core.lock.builder.impl;

import cn.lary.core.lock.MethodExpressEvaluator;
import cn.lary.core.lock.anno.Lock;
import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.config.LockProp;
import cn.lary.core.util.DateKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author paul 2024/4/13
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultLockKeyBuilder implements LockKeyBuilder {

    private final MethodExpressEvaluator methodExpressEvaluator;

    private final LockProp lockProp;

    private final HashMap<String,String> keys = new HashMap<>(8);
    @Override
    public String build(MethodInvocation invocation, Lock lock) {
        if(keys.get(invocation.getMethod().toString()) == null){
            long l = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder(lockProp.getLockKeyPrefix());
            sb.append(":");
            if(StringUtils.hasText(lock.name())) sb.append(lock.name());
            else sb.append(invocation.getMethod().getDeclaringClass()).append(invocation.getMethod().getName());
            sb.append(this.getSpelKey(lock.keys(), invocation));
            log.info("当前:[{}]构建key:[{}]",invocation.hashCode(),sb);
            System.out.println(System.currentTimeMillis()-l);
            keys.put(invocation.getMethod().toString(),sb.toString());
        }
        return keys.get(invocation.getMethod().toString());
    }
    private String getSpelKey(String[] definitionKeys,MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        return Stream.of(definitionKeys)
                .filter(StringUtils::hasText)
                .map(k -> methodExpressEvaluator.getV(method, arguments, k, String.class))
                .collect(Collectors.joining("."));
    }
}
