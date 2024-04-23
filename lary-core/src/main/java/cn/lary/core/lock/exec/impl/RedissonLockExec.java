package cn.lary.core.lock.exec.impl;

import cn.lary.core.exception.lock.LockException;
import cn.lary.core.exception.lock.LockFailException;
import cn.lary.core.lock.exec.AbstractLockExec;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/13
 */


@RequiredArgsConstructor
public class RedissonLockExec extends AbstractLockExec<RLock> {

    private final RedissonClient redissonClient;
    @Override
    public RLock lock(String lockK, String lockV, long expire, long acquireTimeout) {
        try {
            final RLock rlock = redissonClient.getLock(lockK);
            boolean locked = rlock.tryLock(acquireTimeout, expire, TimeUnit.MICROSECONDS);
            return super.instance(locked,rlock);
        } catch (InterruptedException e) {
            throw new LockFailException(e.getMessage());
        }
    }

    @Override
    public boolean unLock(String key, String value, RLock lock) {
        if(lock.isHeldByCurrentThread()) {
            try {
                lock.unlockAsync().get();
                return true;
            } catch (InterruptedException | ExecutionException e) {
                throw new LockException(e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean renewal() {
        return true;
    }
}
