package cn.lary.rpc.client.proxy;

import cn.lary.rpc.client.RpcFuture;

public interface Service<T,P,FN extends SerializableFunction<T>> {
    RpcFuture call(String fucName, Object ... args) throws Exception;
    // lambda call
    RpcFuture call(FN fn,Object ... args) throws Exception;
}
