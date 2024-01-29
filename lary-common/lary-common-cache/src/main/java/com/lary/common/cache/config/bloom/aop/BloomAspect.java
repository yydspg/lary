package com.lary.common.cache.config.bloom.aop;

import com.lary.common.cache.filter.RedisBloomFilter;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author paul 2023/12/31
 */
@Component
@Aspect
@Slf4j
public class BloomAspect {
    @Resource
    private RedisBloomFilter rbf;
    @Pointcut("execution(* com.lary.common.cache.impl.*.*(..))&&@annotation(com.lary.common.cache.config.bloom.annotation.EnableBloom)")
    private void method(){}

    @SneakyThrows
    @Around(value = "method()")
    public Object bloom(ProceedingJoinPoint pjp){
        String[] params = (String[]) pjp.getArgs();
        if (!rbf.isContain(params[0],params[1])){
            return null;
        }
        return  pjp.proceed();
    }
}
