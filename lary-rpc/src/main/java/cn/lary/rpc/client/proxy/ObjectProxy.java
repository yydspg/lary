package cn.lary.rpc.client.proxy;



import cn.lary.rpc.client.*;
import cn.lary.rpc.codec.RpcReq;
import cn.lary.rpc.kit.ServiceKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ObjectProxy<T,P> implements InvocationHandler , Service<T,P, SerializableFunction<T>> {
    private static final Logger log = LoggerFactory.getLogger(ObjectProxy.class);
    private  Class<T> clazz;
    private  String version;

    public ObjectProxy(Class<T> clazz, String version) {
        this.clazz = clazz;
        this.version = version;
    }

    @Override
    public RpcFuture call(String fucName, Object... args) throws Exception {
        String serviceKey = ServiceKit.buildServiceKey(this.clazz.getName(), version);
        RpcClientHandler rpcClientHandler = ConnectManager.getInstance().getRpcClientHandler(serviceKey);
        RpcReq rpcReq = createRpcReq(this.clazz.getName(), fucName, args);
        return rpcClientHandler.sendRpcReq(rpcReq);
    }

    @Override
    public RpcFuture call(SerializableFunction<T> tSerializableFunction, Object... args) throws Exception {
        String serviceKey = ServiceKit.buildServiceKey(this.clazz.getName(), version);
        RpcClientHandler rpcClientHandler = ConnectManager.getInstance().getRpcClientHandler(serviceKey);
        RpcReq rpcReq = createRpcReq(this.clazz.getName(), tSerializableFunction.getName(), args);
        return rpcClientHandler.sendRpcReq(rpcReq);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcReq rpcReq = createRpcReq(method.getDeclaringClass().getName(), method.getName(), args);
        RpcClientHandler rpcClientHandler = ConnectManager.getInstance().getRpcClientHandler(ServiceKit.buildServiceKey(method.getDeclaringClass().getName(), version));
        return rpcClientHandler.sendRpcReq(rpcReq).get();
    }
    public RpcReq createRpcReq(String className,String methodName,Object[] args){
        RpcReq t = new RpcReq();
        t.setReqId(UUID.randomUUID().toString());
        t.setClassName(className);
        t.setMethodName(methodName);
        t.setParameters(args);
        t.setVersion(version);
        Class[] parameterTypes = new Class[args.length];
        // get parameter Type
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        t.setParameterTypes(parameterTypes);
        return t;
    }

}

