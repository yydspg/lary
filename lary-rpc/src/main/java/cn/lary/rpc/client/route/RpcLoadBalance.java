package cn.lary.rpc.client.route;

import cn.lary.rpc.client.ConnectManager;
import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.kit.ServiceKit;
import cn.lary.rpc.protocol.RpcProtocol;
import cn.lary.rpc.protocol.RpcServiceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RpcLoadBalance {

    private Map<String,List<RpcProtocol>> serviceMap;

    private void updateServiceMap(Map<RpcProtocol, RpcClientHandler> serverNodes) {
        // serverNodes ,based on instance to collect data
        if(ConnectManager.updated.get() &&serverNodes != null && serverNodes.size() > 0) {
            Map<String,List<RpcProtocol>> updateServiceMap = new HashMap<>();
            // traverse a node through its interfaces
            for (RpcProtocol rpcProtocol : serverNodes.keySet()) {
                for (RpcServiceInfo serviceInfo : rpcProtocol.getRpcServiceInfos()) {
                    String serviceKey = ServiceKit.buildServiceKey(serviceInfo.getServiceName(), serviceInfo.getVersion());
                    List<RpcProtocol> rpcProtocols = updateServiceMap.get(serviceKey);
                    if (rpcProtocols == null ){
                        rpcProtocols = new ArrayList<>();
                    }
                    //reload instances
                    rpcProtocols.add(rpcProtocol);
                    // storage <k,v> : <service key,instance >
                    updateServiceMap.putIfAbsent(serviceKey,rpcProtocols);
                }
            }
            serviceMap = updateServiceMap;
            ConnectManager.updated.set(false);
        }
    }
    protected List<RpcProtocol> getRpcProtocols(String serviceName,Map<RpcProtocol,RpcClientHandler> serverNodes) throws Exception {
        updateServiceMap(serverNodes);
        List<RpcProtocol> rpcProtocols = serviceMap.get(serviceName);
        if (rpcProtocols == null) { throw new Exception("Can not find service");}
        return rpcProtocols;
    }
    public abstract RpcProtocol route(String serviceKey,Map<RpcProtocol,RpcClientHandler> serverNodes) throws Exception;
}
