package cn.lary.rpc.client.route.impl;

import cn.lary.rpc.client.RpcClientHandler;
import cn.lary.rpc.client.route.RpcLoadBalance;
import cn.lary.rpc.protocol.RpcProtocol;

import java.util.Map;

public class RoundRobin extends RpcLoadBalance {
    @Override
    public RpcProtocol route(String serviceKey, Map<RpcProtocol, RpcClientHandler> serverNodes) throws Exception {
        return null;
    }
}
