package cn.lary.core.lock.aop;

/**
 * @author paul 2024/4/13
 */
@FunctionalInterface
public interface ThrowableFunction<T,R> {
    R apply(T t) throws Throwable;
}
