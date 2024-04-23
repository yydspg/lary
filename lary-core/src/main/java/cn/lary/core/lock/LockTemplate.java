package cn.lary.core.lock;

import cn.lary.core.exception.lock.LockFailException;
import cn.lary.core.lock.config.LockProp;
import cn.lary.core.lock.exec.LockExec;
import cn.lary.core.util.DateKit;
import cn.lary.core.util.LockKit;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * proxy the real distributed lock template ,such as RedissonLock
 * @author paul 2024/4/13
 */
@Slf4j
public class LockTemplate implements InitializingBean {
    private final Map<Class<? extends LockExec>, LockExec> execMap = new LinkedHashMap<>();

    @Setter
    private LockProp prop;
    @Setter
    private List<LockExec> execs;
    private LockExec primaryExec;

    public LockInfo lock(String key) {
        return lock(key, 0, -1);
    }

    public LockInfo lock(String key, long expire, long acquireTimeout) {
        return lock(key, expire, acquireTimeout, null);
    }
    public LockInfo lock(String lockK,long expire,long acquireTimeout,Class<? extends LockExec> exec) {
        // check
        acquireTimeout = (acquireTimeout < 0) ? prop.getAcquireTimeout() : acquireTimeout;
        LockExec lockExec = this.getExec(exec);
        expire = (!lockExec.renewal()&& expire <= 0) ? prop.getExpire() : expire;
        // TODO 2024/4/24 : 编写一个编译期,检查注解内部值是否有效的Bean
        Long retryInterval = prop.getRetryInterval();
        long start = DateKit.currentTimeMillis();
        String lockV = LockKit.getLockV();
        int acquireCount = 0;
        try {
            do {
                acquireCount++;
                log.info(lockK);
                Object lockInstance = lockExec.lock(lockK, lockV, expire, acquireTimeout);
                if (lockInstance != null) {
                    return new LockInfo(lockK,lockV,expire,acquireTimeout,acquireCount,lockInstance,lockExec);
                }
                TimeUnit.MILLISECONDS.sleep(retryInterval);
            }while  (DateKit.currentTimeMillis() - start < acquireTimeout);
        } catch (InterruptedException e) {
            throw new LockFailException(e.getMessage());
        }
        return null;
    }

    public boolean unLock(LockInfo lockInfo) {
        if(null == lockInfo ) return false;
        return lockInfo.getLockExec().unLock(lockInfo.getLockK(), lockInfo.getLockV(), lockInfo.getLockInstance());
    }

    protected LockExec getExec(Class<? extends LockExec> clz) {
        if (null == clz || clz == LockExec.class) {
            return primaryExec;
        }
        return execMap.get(clz);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.isTrue(prop.getAcquireTimeout() >= 0, "tryTimeout must least 0");
        Assert.isTrue(prop.getExpire() >= -1, "expireTime must lease -1");
        Assert.isTrue(prop.getRetryInterval() >= 0, "retryInterval must more than 0");
        Assert.hasText(prop.getLockKeyPrefix(), "lock key prefix must be not blank");
        Assert.notEmpty(execs, "executors must have at least one");
        // TODO 2024/4/23 : 为什么 execs 存在两个redissonLock
        for (LockExec executor : execs) {
            execMap.put(executor.getClass(), executor);
        }
        Class<? extends LockExec> setExec = prop.getPrimaryExecutor();
        this.primaryExec = setExec == null ? execs.get(0) : execMap.get(setExec);
        Assert.notNull(this.primaryExec,"primaryExecutor must not be null");
    }
}
