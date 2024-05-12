package cn.lary.rpc.client;

import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.client.proxy.ObjectProxy;
import cn.lary.rpc.client.proxy.Service;
import cn.lary.rpc.kit.ThreadPoolKit;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcClient implements ApplicationContextAware, DisposableBean {

    private final ServiceDiscovery serviceDiscovery;
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolKit.createThreadPoolExecutor(RpcClient.class.getSimpleName(),8,16);

    public RpcClient(String serverAddress) {
        this.serviceDiscovery = new ServiceDiscovery(serverAddress);
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
        ConnectManager.getInstance().stop();
    }
    public static <T,P> T createService(Class<T> interfaceClass,String version) {
        return  (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T,P>(interfaceClass,version)
        );
    }
    // automatic destroy service
    @Override
    public void destroy() throws Exception {
        this.stop();
    }
    // fill all fields which decorated by @interface : RpcInject
    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                RpcInject rpcInject = field.getAnnotation(RpcInject.class);
                if(rpcInject != null){
                    String version = rpcInject.version();
                    field.setAccessible(true);
                    field.set(bean,createService(field.getType(),version));
                }
            }
        }
    }

}
