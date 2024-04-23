package cn.lary.core.lock.exec;

import cn.lary.core.lock.exec.LockExec;

/**
 * @author paul 2024/4/13
 */

public abstract class AbstractLockExec<T> implements LockExec<T> {

    protected T instance(boolean locked, T lockInstance) {
        return locked ? lockInstance : null;
    }

}
