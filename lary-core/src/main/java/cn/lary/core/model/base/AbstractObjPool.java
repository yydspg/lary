package cn.lary.core.model.base;

import cn.lary.core.exception.BizException;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paul 2024/4/17
 */

public class AbstractObjPool<T> {

    private final GenericObjectPool<T> genericObjectPool;

    protected AbstractObjPool(@Nonnull PooledObjectFactory<T> pooledObjectFactory, @Nonnull Pool pool) {
        GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(pool.getMaxTotal());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        config.setMaxWait(pool.getMaxWait());
        config.setMinEvictableIdleDuration(pool.getMinEvictableIdleDuration());
        config.setSoftMinEvictableIdleDuration(pool.getSoftMinEvictableIdleDuration());
        config.setLifo(pool.getLifo());
        config.setBlockWhenExhausted(pool.getBlockWhenExhausted());
        genericObjectPool = new GenericObjectPool<>(pooledObjectFactory, config);
    }

    public T get() {
        try {
            return genericObjectPool.borrowObject();
        } catch (Exception e) {
            throw new BizException("Can not fetch object from pool.");
        }
    }

    public void close(T client) {
        if (null != client) {
            genericObjectPool.returnObject(client);
        }
    }
}
