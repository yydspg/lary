package cn.lary.rpc.client;

import cn.lary.rpc.kit.ThreadPoolKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcClient implements ApplicationContextAware, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(RpcClient.class);
    private final ServiceDiscovery serviceDiscovery;
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolKit.createThreadPoolExecutor(RpcClient.class.getSimpleName(),8,16);
    public RpcClient(String serverAddress) {
        this.serviceDiscovery = new ServiceDiscovery(serverAddress);
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }
}
