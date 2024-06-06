package cn.lary.rpc.client.route.impl;

import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.protocol.RpcProtocol;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LFULoadBalance extends RpcLoadBalance {
    private final ConcurrentHashMap<String, HashMap<RpcProtocol,Integer>> LFUMap = new ConcurrentHashMap<>();
    private long cache_time = 0;
    @Override
    public RpcProtocol route(String serviceKey, Map<String, RpcClientHandler> serverNodes) throws Exception {
        List<RpcProtocol> rpcProtocols = getRpcProtocols(serviceKey, serverNodes);
        // clear cache
        if(System.currentTimeMillis() > cache_time){
            LFUMap.clear();
            // every day
            cache_time = System.currentTimeMillis() + 1000 * 60* 60 * 24;
        }
        // initialize the list under this serviceKey
        HashMap<RpcProtocol, Integer> map = LFUMap.computeIfAbsent(serviceKey, k -> new HashMap<>());

        // add new
        for (RpcProtocol t : rpcProtocols) {
            if(!map.containsKey(t) || map.get(t) > 10000){
                map.put(t, 0);
            }
        }
        // remove old
        ArrayList<Object> delKeys = new ArrayList<>();
        for (RpcProtocol t : map.keySet()) {
            if(!rpcProtocols.contains(t)){
                delKeys.add(t);
            }
        }
        if(delKeys.size() > 0){
            delKeys.forEach(map::remove);
        }
        List<Map.Entry<RpcProtocol, Integer>> list = map.entrySet().stream().collect(Collectors.toList());
        Collections.sort(list, new Comparator<Map.Entry<RpcProtocol, Integer>>() {
            @Override
            public int compare(Map.Entry<RpcProtocol, Integer> o1, Map.Entry<RpcProtocol, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Map.Entry<RpcProtocol, Integer> entry = list.get(0);
        RpcProtocol key = entry.getKey();
        entry.setValue(entry.getValue() + 1);
        return key;
    }
}
