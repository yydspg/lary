package cn.lary.rpc.client.route;

import cn.lary.rpc.client.ServiceDiscovery;
import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.kit.ServiceKit;
import cn.lary.rpc.protocol.RpcProtocol;
import cn.lary.rpc.protocol.RpcServiceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RpcLoadBalance {

    public Map<String,List<RpcProtocol>> serviceMap;

    private void updateServiceMap(Map<String, RpcClientHandler> serverNodes) {
        // serverNodes ,based on instance to collect data
        if(ServiceDiscovery.updated.get() &&serverNodes != null && serverNodes.size() > 0) {
            Map<String,List<RpcProtocol>> updateServiceMap = new HashMap<>();
            // traverse a node through its interfaces
            for (RpcClientHandler rpcClientHandler : serverNodes.values()) {
                for (RpcServiceInfo serviceInfo : rpcClientHandler.rpcProtocol.getRpcServiceInfos()) {
                    String serviceKey = ServiceKit.buildServiceKey(serviceInfo.getServiceName(), serviceInfo.getVersion());
                    List<RpcProtocol> rpcProtocols = updateServiceMap.get(serviceKey);
                    if (rpcProtocols == null ){
                        rpcProtocols = new ArrayList<>();
                    }
                    //reload instances
                    rpcProtocols.add(rpcClientHandler.rpcProtocol);
                    // storage <k,v> : <service key,instance >
                    updateServiceMap.putIfAbsent(serviceKey,rpcProtocols);
                }
            }
            serviceMap = updateServiceMap;
            ServiceDiscovery.updated.set(false);
        }
    }
    protected List<RpcProtocol> getRpcProtocols(String serviceName,Map<String,RpcClientHandler> serverNodes) throws Exception {
        updateServiceMap(serverNodes);
        List<RpcProtocol> rpcProtocols = serviceMap.get(serviceName);
        if (rpcProtocols == null) { throw new Exception("Can not find service");}
        return rpcProtocols;
    }
    public abstract RpcProtocol route(String serviceKey,Map<String,RpcClientHandler> serverNodes) throws Exception;
    public void addProtocol(String serviceName,RpcProtocol rpcProtocol){
        if(!serviceMap.containsKey(serviceName)){
            serviceMap.put(serviceName,new ArrayList<>());
        }
        if(!serviceMap.get(serviceName).contains(rpcProtocol)) {
            serviceMap.get(serviceName).add(rpcProtocol);
        }
    }
}
