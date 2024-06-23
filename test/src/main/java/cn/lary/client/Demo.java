package cn.lary.client;

import cn.lary.api.DemoService;
import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.client.RpcClient;

public class Demo {
    @RpcInject
    public DemoService demoService;

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        Demo demo = new Demo();
//        String man = demo.demoService.say("man");
//        System.out.println(man);
    }
}
