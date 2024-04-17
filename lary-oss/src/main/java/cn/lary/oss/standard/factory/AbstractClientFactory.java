package cn.lary.oss.standard.factory;

import cn.lary.oss.standard.prop.AbstractProp;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author paul 2024/4/17
 */
@RequiredArgsConstructor
public abstract class AbstractClientFactory<T> extends BasePooledObjectFactory<T> {

    private final AbstractProp abstractProp;

    public AbstractProp getProp() {return abstractProp;}
    // TODO 2024/4/17 : 了解此方法的作用
    @Override
    public PooledObject<T> wrap(T obj) {return new DefaultPooledObject<>(obj);}

}
