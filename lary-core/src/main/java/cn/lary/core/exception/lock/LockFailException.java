package cn.lary.core.exception.lock;

import cn.lary.core.exception.lock.LockException;

/**
 * @author paul 2024/4/13
 */

public class LockFailException  extends LockException {
    public LockFailException(String message) {
        super(message);
    }
}
