package cn.lary.rpc.client.route.impl;

import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.protocol.RpcProtocol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRULoadBalance extends RpcLoadBalance {
    private long cache_time = 0;
    // TODO  :  try to figure out this segment
    private ConcurrentHashMap<String, LinkedHashMap<RpcProtocol,RpcProtocol>> LRUMap = new ConcurrentHashMap<>();
    @Override
    public RpcProtocol route(String serviceKey, Map<RpcProtocol, RpcClientHandler> serverNodes) throws Exception {
        List<RpcProtocol> rpcProtocols = getRpcProtocols(serviceKey, serverNodes);
        // cache clear
        if(System.currentTimeMillis() > cache_time){
            LRUMap.clear();
            cache_time = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }
        // init lru
        LinkedHashMap<RpcProtocol, RpcProtocol>  map = LRUMap.get(serviceKey);
        if(map == null){
            map = new LinkedHashMap<RpcProtocol,RpcProtocol>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<RpcProtocol, RpcProtocol> eldest) {
                    return super.size() > 100;
                }
            };
            LRUMap.putIfAbsent(serviceKey, map);
        }
        // add new
        for (RpcProtocol t : rpcProtocols) {
            if(!map.containsKey(t)) map.put(t,t);
        }
        // remove old
        ArrayList<RpcProtocol> delKeys = new ArrayList<>();
        for (RpcProtocol t : map.keySet()) {
            if(!rpcProtocols.contains(t)) delKeys.add(t);
        }
        if(delKeys.size() > 0){
            delKeys.forEach(map::remove);
        }
        RpcProtocol eldestKey = map.entrySet().iterator().next().getKey();
        return map.get(eldestKey);
    }
}
