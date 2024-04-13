package cn.lary.core.lock.builder;

import cn.lary.core.lock.anno.Lock;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author paul 2024/4/13
 */

public interface LockKeyBuilder {
    String build(MethodInvocation invocation, Lock lock);
}
