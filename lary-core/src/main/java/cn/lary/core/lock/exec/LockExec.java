package cn.lary.core.lock.exec;

/**
 * @author paul 2024/4/13
 */

public interface LockExec<T> {
    T lock(String lockK, String lockV, long expire, long acquireTimeout);
    boolean unLock(String key, String value, T lockInstance);
    default boolean renewal() {
        return false;
    }
}
