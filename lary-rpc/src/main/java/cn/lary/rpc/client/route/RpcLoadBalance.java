package cn.lary.rpc.client.route;

import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.kit.ServiceKit;
import cn.lary.rpc.protocol.RpcProtocol;
import cn.lary.rpc.protocol.RpcServiceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RpcLoadBalance {
    protected Map<String, List<RpcProtocol>>  getServiceMap(Map<RpcProtocol, RpcClientHandler> serverNodes) {
        Map<String,List<RpcProtocol>> serviceMap = new HashMap<>();
        // serverNodes ,based on instance to collect data
        if(serverNodes != null && serverNodes.size() > 0) {
            // traverse a node through its interfaces
            for (RpcProtocol rpcProtocol : serverNodes.keySet()) {
                for (RpcServiceInfo serviceInfo : rpcProtocol.getRpcServiceInfos()) {
                    String serviceKey = ServiceKit.buildServiceKey(serviceInfo.getServiceName(), serviceInfo.getVersion());
                    List<RpcProtocol> rpcProtocols = serviceMap.get(serviceKey);
                    if (rpcProtocols == null ){
                        rpcProtocols = new ArrayList<>();
                    }
                    //reload instances
                    rpcProtocols.add(rpcProtocol);
                    // storage <k,v> : <service key,instance >
                    serviceMap.putIfAbsent(serviceKey,rpcProtocols);
                }
            }
        }
        return serviceMap;
    }

    public abstract RpcProtocol route(String serviceKey,Map<RpcProtocol,RpcClientHandler> serverNodes) throws Exception;
}
