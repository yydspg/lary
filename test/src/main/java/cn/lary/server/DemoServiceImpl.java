package cn.lary.server;

import cn.lary.api.DemoService;
import cn.lary.rpc.anno.RpcService;

@RpcService(interfaceClass = DemoService.class)
public class DemoServiceImpl implements DemoService {
    @Override
    public String say(String name) {
        return "what can i say"+name;
    }
}
