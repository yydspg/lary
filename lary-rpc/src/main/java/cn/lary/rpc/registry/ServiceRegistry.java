package cn.lary.rpc.registry;

import cn.lary.rpc.config.SystemProperties;
import cn.lary.rpc.protocol.RpcProtocol;
import cn.lary.rpc.protocol.RpcServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceRegistry {

        private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

        private RpcProtocol rpcProtocol;
        private final RegistryClient registryClient;
        public final String registerAddress;

        public ServiceRegistry(String registryServerAddress) {
                registryClient = new NacosRegistryClient(registryServerAddress);
                registerAddress  =registryServerAddress;
        }

        public void registerService(Map<String,Object> serviceMap){
                // Register service info
                List<RpcServiceInfo> serviceInfoList = new ArrayList<>();
                for (String key : serviceMap.keySet()) {
                        if (key != null && !key.isBlank()) {
                                RpcServiceInfo rpcServiceInfo = new RpcServiceInfo();
                                String[] args = key.split(":");
                                rpcServiceInfo.setServiceName(args[0]);
                                if (args[1] != null) {
                                        rpcServiceInfo.setVersion(args[1]);
                                } else {
                                        rpcServiceInfo.setVersion("");
                                }
                                serviceInfoList.add(rpcServiceInfo);
                        } else {
                                log.warn("Can not get service name and version: [{}] ", key);
                        }
                }
                rpcProtocol = new RpcProtocol();
                String[] args = registerAddress.split(":");
                rpcProtocol.setHost(args[0]);
                rpcProtocol.setPort(Integer.parseInt(args[1]));
                rpcProtocol.setRpcServiceInfos(serviceInfoList);
                // batch register service
                for (RpcServiceInfo t : serviceInfoList) {
                        registryClient.login(SystemProperties.getHost(),SystemProperties.getPort(),t.getServiceName(),t.getVersion());
                        log.info("Register new service: [{}] ", t.getServiceName());
                }
        }
        public void deregisterService(){
                // deregister service
                rpcProtocol.getRpcServiceInfos().forEach(t -> {
                        registryClient.logout(rpcProtocol.getHost(), rpcProtocol.getPort(), t.getServiceName(),t.getVersion());
                        log.info("Deregister service: [{}] ", t.getServiceName());
                });
        }


}

