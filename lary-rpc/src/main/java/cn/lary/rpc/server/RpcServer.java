package cn.lary.rpc.server;

import cn.lary.rpc.anno.RpcService;
import cn.lary.rpc.registry.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class RpcServer extends NettyServer implements ApplicationContextAware, InitializingBean, DisposableBean {
    public RpcServer(String serverAddress, String registerAddress) {
        super(serverAddress, registerAddress);
    }

    @Override
    public void destroy() throws Exception {
        super.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // collect rpc service beans
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
            for (Object service : serviceMap.values()) {
                RpcService rpcServiceAnno = service.getClass().getAnnotation(RpcService.class);
                String name = rpcServiceAnno.interfaceClass().getName();
                String version = rpcServiceAnno.version();
                super.addService(name, version,service);
            }
    }
}
