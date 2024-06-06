package cn.lary.rpc.test.service;

import cn.lary.rpc.anno.RpcService;
import cn.lary.rpc.test.api.IService;

@RpcService(interfaceClass = IService.class,version = "1.0")
public class PService {
}
