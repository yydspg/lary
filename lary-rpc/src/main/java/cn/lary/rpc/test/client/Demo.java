package cn.lary.rpc.test.client;

import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.client.RpcClient;
import cn.lary.rpc.test.api.IService;

public class Demo {
    @RpcInject(version = "1.0")
    private IService service;

    public static void main(String[] args) {
        Demo demo = new Demo();
        RpcClient rpcClient = new RpcClient();

    }
}
