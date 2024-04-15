package cn.lary.core.lock.lockFailPloy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author paul 2024/4/13
 */
@Slf4j
public class DefaultLockFailPloy implements LockFailPloy {
    @Override
    public void onLockFail(String key, Method method, Object[] arguments) {
        log.warn("lockFail");
    }
}
