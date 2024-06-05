package cn.lary.rpc.test.server;

import cn.lary.rpc.server.RpcServer;

public class PServer {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1:9001";
        String registryIp = "127.0.0.1:8848";
        RpcServer rpcServer = new RpcServer(serverIp, registryIp);
        System.out.println("try to start server");
        rpcServer.start();
    }
}
