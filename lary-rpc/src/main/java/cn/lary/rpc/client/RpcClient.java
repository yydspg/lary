package cn.lary.rpc.client;

import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.client.proxy.ObjectProxy;
import cn.lary.rpc.client.proxy.Service;
import cn.lary.rpc.kit.ThreadPoolKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.ThreadPoolExecutor;
@Slf4j
public class RpcClient implements ApplicationContextAware{

    private ServiceDiscovery serviceDiscovery;
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolKit.createThreadPoolExecutor(RpcClient.class.getSimpleName(),8,16);
    public RpcClient() {
        serviceDiscovery = ServiceDiscovery.getInstance();
        serviceDiscovery.loadData();
    }

    public static <T, P> Service createAsyncService(Class<T> interfaceClass, String version) {
        return new ObjectProxy<T, P>(interfaceClass, version);
    }
    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }
    // shutdown client server
    public void stop (){
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
    }
    // create proxy obj
    public static <T,P> T createService(Class<T> interfaceClass,String version) {
        return  (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T,P>(interfaceClass,version)
        );
    }
    // fill all fields which decorated by @interface : RpcInject
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();
            try {
                for (Field field : fields) {
                    RpcInject rpcAutowired = field.getAnnotation(RpcInject.class);
                    if (rpcAutowired != null) {
                        String version = rpcAutowired.version();
                        field.setAccessible(true);
                        field.set(bean, createService(field.getType(), version));
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(e.toString());
            }
        }
    }
}
