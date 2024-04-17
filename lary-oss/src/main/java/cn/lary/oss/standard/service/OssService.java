package cn.lary.oss.standard.service;

import cn.lary.core.model.base.AbstractObjPool;
import lombok.RequiredArgsConstructor;

/**
 * @author paul 2024/4/17
 */
@RequiredArgsConstructor
public abstract class OssService<T> {
    private final AbstractObjPool<T> objectPool;
    protected T getCli() {
        return objectPool.get();
    }
    protected void close(T client) {
        objectPool.close(client);
    }
}
