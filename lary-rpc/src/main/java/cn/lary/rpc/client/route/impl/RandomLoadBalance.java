package cn.lary.rpc.client.route.impl;

import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.protocol.RpcProtocol;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalance extends RpcLoadBalance {
    private final Random random = new Random();

    @Override
    public RpcProtocol route(String serviceKey, Map<String, RpcClientHandler> serverNodes) throws Exception {
        List<RpcProtocol> rpcProtocols = getRpcProtocols(serviceKey, serverNodes);
        return rpcProtocols.get(random.nextInt(rpcProtocols.size()));
    }
}
