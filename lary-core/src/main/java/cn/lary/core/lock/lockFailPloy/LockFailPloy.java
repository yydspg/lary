package cn.lary.core.lock.lockFailPloy;

import java.lang.reflect.Method;

/**
 * @author paul 2024/4/13
 */

public interface LockFailPloy {
    void onLockFail(String key, Method method, Object[] arguments);

}
