package cn.lary.rpc.server;

import cn.lary.rpc.anno.RpcService;
import cn.lary.rpc.kit.ReflectKit;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RpcServer extends NettyServer {
    private ReflectKit reflectKit;
    public RpcServer(String serverAddress) {
        super(serverAddress);
        reflectKit = ReflectKit.get();
        setService();
    }
    public void setService()  {
        // collect rpc service beans
        Set<Class<?>> set = reflectKit.getBeans(RpcService.class);
        for (Object service : set.stream().map(this::get).collect(Collectors.toSet())) {
                RpcService rpcServiceAnno = service.getClass().getAnnotation(RpcService.class);
                String name = rpcServiceAnno.interfaceClass().getName();
                String version = rpcServiceAnno.version();
                super.addService(name, version,service);
            }
    }
    private Object get(Class clz) {
        Object obj = null;
        try {
            obj = clz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }
}
