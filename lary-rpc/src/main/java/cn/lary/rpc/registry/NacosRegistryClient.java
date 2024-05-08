package cn.lary.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NacosRegistryClient implements RegistryClient {
    private final NamingService namingService;
    private static final Logger log = LoggerFactory.getLogger(NacosRegistryClient.class);

    public NacosRegistryClient(String registerAddress) {
        try {
                namingService = NamingFactory.createNamingService(registerAddress);
        } catch (NacosException e) {
            log.error("nacos namingService error[{}]", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void login(String instanceHost, int instancePort, String name, String version) {
        try {
            Instance t = new Instance();
            t.setIp(instanceHost);
            t.setPort(instancePort);
            namingService.registerInstance(name+":"+version, t);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(String instanceHost, int instancePort, String name,String version) {
        try {
            namingService.deregisterInstance(name+":"+version,instanceHost,instancePort);
        } catch (NacosException e) {
            log.error("logout error[[{}]",e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
