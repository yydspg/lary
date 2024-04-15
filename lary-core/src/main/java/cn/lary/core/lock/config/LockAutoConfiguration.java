package cn.lary.core.lock.config;

import cn.lary.core.lock.LockTemplate;
import cn.lary.core.lock.MethodExpressEvaluator;
import cn.lary.core.lock.SpelMethodExpressEvaluator;
import cn.lary.core.lock.aop.LockAdvisor;
import cn.lary.core.lock.aop.LockMethodInterceptor;
import cn.lary.core.lock.aop.impl.LockOpsInterceptor;
import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.builder.impl.DefaultLockKeyBuilder;
import cn.lary.core.lock.exec.LockExec;
import cn.lary.core.lock.exec.impl.LocalLockExec;
import cn.lary.core.lock.lockFailPloy.DefaultLockFailPloy;
import cn.lary.core.lock.lockFailPloy.LockFailPloy;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author paul 2024/4/13
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration(proxyBeanMethods = false)
public class LockAutoConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(MethodExpressEvaluator.class)
    public SpelMethodExpressEvaluator SpelMethodBasedExpressionEvaluator() {
        return new SpelMethodExpressEvaluator();
    }
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LockProp lockProp() {return new LockProp();}
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public LockTemplate lockTemplate(List<LockExec> execs,LockProp lockProp) {
        LockTemplate lockTemplate = new LockTemplate();
        lockTemplate.setExecs(execs);
        lockTemplate.setProp(lockProp);
        return lockTemplate;
    }
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnMissingBean
    public LockKeyBuilder lockKeyBuilder(MethodExpressEvaluator methodExpressEvaluator,LockProp lockProp) {
        return new DefaultLockKeyBuilder(methodExpressEvaluator,lockProp);
    }
    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LockFailPloy lockFailPloy() {
        return new DefaultLockFailPloy();
    }
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LocalLockExec localLockExecutor() {
        return new LocalLockExec();
    }
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnMissingBean
    public LockAdvisor lockAnnotationAdvisor(LockMethodInterceptor lockInterceptor) {
        return new LockAdvisor(lockInterceptor, Ordered.HIGHEST_PRECEDENCE);
    }
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Bean
    @ConditionalOnMissingBean(LockMethodInterceptor.class)
    public LockOpsInterceptor conditionalLockOpsInterceptor(
            MethodExpressEvaluator methodExpressEvaluator,
            LockProp lockProp, LockTemplate lockTemplate) {
        return new LockOpsInterceptor(methodExpressEvaluator, lockTemplate, lockProp);
    }
}
