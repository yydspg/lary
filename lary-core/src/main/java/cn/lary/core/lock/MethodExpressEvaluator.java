package cn.lary.core.lock;

import java.lang.reflect.Method;

/**
 * @author paul 2024/4/13
 */

public interface MethodExpressEvaluator {
    <T> T getV(Method method, Object[] args, String express, Class<T> resultType);
}
