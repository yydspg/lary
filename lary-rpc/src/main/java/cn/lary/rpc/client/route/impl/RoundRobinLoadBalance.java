package cn.lary.rpc.client.route.impl;

import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.protocol.RpcProtocol;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance extends RpcLoadBalance {
    private final ConcurrentHashMap<String, AtomicInteger>  roundRobins =  new ConcurrentHashMap<>();
    @Override
    public RpcProtocol route(String serviceKey, Map<RpcProtocol, RpcClientHandler> serverNodes) throws Exception {
        List<RpcProtocol> rpcProtocols = getRpcProtocols(serviceKey, serverNodes);
        roundRobins.putIfAbsent(serviceKey, new AtomicInteger(0));
        AtomicInteger t = roundRobins.get(serviceKey);
        int size = rpcProtocols.size();
        int index = (t.getAndAdd(1) + size) % size;
        return rpcProtocols.get(index);
    }
}
