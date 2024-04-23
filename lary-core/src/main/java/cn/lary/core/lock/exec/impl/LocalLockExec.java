package cn.lary.core.lock.exec.impl;

import cn.lary.core.lock.exec.AbstractLockExec;
import cn.lary.core.util.DateKit;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author paul 2024/4/13
 */
@NoArgsConstructor
@AllArgsConstructor
public class LocalLockExec extends AbstractLockExec<LocalLockExec.LocalLock> {

    private boolean fair = true;

    private final ConcurrentMap<String, LocalLock> lockMap = new ConcurrentReferenceHashMap<>(
            32, ConcurrentReferenceHashMap.ReferenceType.WEAK
    );

    @Override
    public LocalLock lock(String lockK, String lockV, long expire, long acquireTimeout) {
        return null;
    }

    @Override
    public boolean unLock(String key, String value, LocalLock lockInstance) {
        return false;
    }

    public static class LocalLock extends ReentrantLock {
        public static final long NEVER_EXPIRE = -1L;
        private  long expireTime;

        LocalLock(boolean fair, long expireTime) {
            super(fair);
            this.expireTime = expireTime;
        }
        public boolean isExpired() {
            return expireTime != NEVER_EXPIRE
                    && DateKit.currentTimeMillis() > expireTime;
        }
        public void resetExpire(long expire) {
            this.expireTime = expire < 0 ?
                    NEVER_EXPIRE : DateKit.currentTimeMillis() + expire;
        }
    }
}
