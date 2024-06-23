package cn.lary.rpc.registry;

import cn.lary.rpc.core.SystemConfig;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class NacosRegistryClient implements RegistryClient {
    private final Properties nacosConfig = new Properties();
    private final NamingService namingService;
    private static final Logger log = LoggerFactory.getLogger(NacosRegistryClient.class);
    private static final NacosRegistryClient nacosRegistryClient;
    static {
        nacosRegistryClient = new NacosRegistryClient();
    }
    public static NacosRegistryClient getInstance() {
        return nacosRegistryClient;
    }
    private NacosRegistryClient() {
        try {
                log.info("NacosRegistryClient register address: {}",SystemConfig.registerAddress);
                nacosConfig.put(PropertyKeyConst.SERVER_ADDR, SystemConfig.registerAddress);
                nacosConfig.put(PropertyKeyConst.NAMESPACE, SystemConfig.namespace);
                namingService = NamingFactory.createNamingService(nacosConfig);

        } catch (NacosException e) {
            log.error("nacos namingService error[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void login(String instanceHost, int instancePort, String name, String version) {
        try {
            namingService.registerInstance(name+":"+version,SystemConfig.group,instanceHost,instancePort);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(String instanceHost, int instancePort, String name,String version) {
        try {
            namingService.deregisterInstance(name+":"+version,SystemConfig.group,instanceHost,instancePort);
        } catch (NacosException e) {
            log.error("logout error[[{}]",e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public List<Instance> getAllInstances(String serviceName, String group, String version) {
        try {
            return namingService.getAllInstances(serviceName+":"+version,group);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
    public void subscribe(String serviceName, String group, String version,EventListener listener) {
        try {
            namingService.subscribe(serviceName+":"+version,group,listener);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
