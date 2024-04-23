package cn.lary.core.lock.aop.impl;

import cn.lary.core.lock.anno.Lock;
import cn.lary.core.lock.aop.LockMethodInterceptor;
import cn.lary.core.lock.aop.ThrowableFunction;
import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.lockFailPloy.LockFailPloy;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author paul 2024/4/13
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractLockInterceptor implements InitializingBean, ApplicationContextAware, LockMethodInterceptor {

    private static final LockOps NULL = new NullLockOps();

    private final Map<Method, LockOps> lockOpsCaches = new ConcurrentReferenceHashMap<>(16);


    protected ApplicationContext applicationContext;

    private LockOps defaultLockOps;
    @NonNull
    protected abstract LockOps initDefaultLockOps();

    protected abstract Object doLock(LockOps lockOps, MethodInvocation invocation) throws Throwable;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        if (null == target) {
            return invocation.proceed();
        }
        Class<?> cls = AopProxyUtils.ultimateTargetClass(target);
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        LockOps lockOps = lockOpsCaches.computeIfAbsent(
                invocation.getMethod(),
                method -> Optional.ofNullable(resolveLockOps(invocation)).orElse(NULL)
        );
        MethodInvocation decorator = lockOps.attach(invocation);
        return decorator.proceed();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.defaultLockOps = initDefaultLockOps();
        Assert.notNull(applicationContext,"ApplicationContextMustNotBeNull");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
    }
    @Nullable
    protected LockOps resolveLockOps(MethodInvocation invocation) {
        Lock lock = AnnotatedElementUtils.findMergedAnnotation(invocation.getMethod(), Lock.class);
        return ( lock != null) ?
                createLockOps(lock) : null;
    }

    protected LockOps createLockOps(Lock lock) {

        // Obtain an instance based on the keyBuilder configured in the annotation
        LockKeyBuilder resK = Optional.ofNullable(lock.keyBuilderStrategy())
                .filter(t -> !Objects.equals(t, LockKeyBuilder.class))
                .map(applicationContext::getBeansOfType)
                .map(Map::values)
                .flatMap(t -> t.stream().min(AnnotationAwareOrderComparator.INSTANCE))
                .map(LockKeyBuilder.class::cast)
                .orElse(defaultLockOps.getLockKeyBuilder());

        // Obtain an instance based on the LockFailPloy configured in the annotation
        LockFailPloy resF = Optional.ofNullable(lock.failStrategy())
                .filter(t -> !Objects.equals(t, LockFailPloy.class))
                .map(applicationContext::getBeansOfType)
                .map(Map::values)
                .flatMap(t -> t.stream().min(AnnotationAwareOrderComparator.INSTANCE))
                .map(LockFailPloy.class::cast)
                .orElse(defaultLockOps.getLockFailPloy());
        return new LockOpsImpl(lock)
                .setLockFailPloy(resF)
                .setLockKeyBuilder(resK);

    }
    protected interface LockOps extends Ordered {
        Lock getAnno();

        LockKeyBuilder getLockKeyBuilder();

        LockFailPloy getLockFailPloy();

        @Override
        default int getOrder() {
            return getAnno().order();
        }

        default MethodInvocation attach(MethodInvocation invocation) {
            return invocation;
        }
    }


    // null  placeholder
    private static class NullLockOps implements LockOps {
        @Override
        public Lock getAnno() {
            return null;
        }
        @Override
        public LockKeyBuilder getLockKeyBuilder() {
            return null;
        }
        @Override
        public LockFailPloy getLockFailPloy() {
            return null;
        }
    }


    // default lockOps impl
    @Accessors(chain = true)
    @Getter
    @Setter
    @RequiredArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    protected class LockOpsImpl implements LockOps {
        @EqualsAndHashCode.Include
        private final Lock anno;

        private LockKeyBuilder lockKeyBuilder;

        private LockFailPloy lockFailPloy;

        private int order = Ordered.LOWEST_PRECEDENCE;
        //Decorator mode
        @Override
        public MethodInvocation attach(MethodInvocation invocation) {
            return new DelegatedMethodInvocation(invocation, inv -> doLock(this, inv));
        }
    }
    // lock ops delegate
    @RequiredArgsConstructor
    protected abstract static class AbstractLockDelegate implements LockOps {
        protected final LockOps delegate;
        @Override
        public Lock getAnno() {
            return delegate.getAnno();
        }
        @Override
        public LockKeyBuilder getLockKeyBuilder() {
            return delegate.getLockKeyBuilder();
        }
        @Override
        public LockFailPloy getLockFailPloy() {
            return delegate.getLockFailPloy();
        }
        @Override
        public int getOrder() {
            return delegate.getOrder();
        }
    }
    @RequiredArgsConstructor
    private static class DelegatedMethodInvocation implements MethodInvocation {

        private final MethodInvocation delegate;
        private final ThrowableFunction<MethodInvocation, Object> invoker;
        @Override
        public Object proceed() throws Throwable {
            // interceptor
            return invoker.apply(delegate);
        }
        @Override
        public Object getThis() {
            return delegate.getThis();
        }
        @NonNull
        @Override
        public AccessibleObject getStaticPart() {
            return delegate.getStaticPart();
        }
        @NonNull
        @Override
        public Method getMethod() {
            return delegate.getMethod();
        }
        @NonNull
        @Override
        public Object[] getArguments() {
            return delegate.getArguments();
        }
    }
}
