package cn.lary.server;

import cn.lary.rpc.core.SystemConfig;
import cn.lary.rpc.server.RpcServer;

public class Test {
    public static void main(String[] args) {
        SystemConfig.packageAddress = "cn.lary";
        RpcServer rpcServer = new RpcServer("127.0.0.1:9002");
        rpcServer.start();
    }
}
