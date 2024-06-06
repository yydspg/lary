package cn.lary.rpc.test;

import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.anno.RpcService;
import cn.lary.rpc.kit.ReflectKit;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Set;

public class Demo implements Serializable {
    @RpcInject
    public Object a;
    @Serial
    public static void main(String[] args) {
        ReflectKit r = new ReflectKit("cn.lary.rpc.test");
        Set<Field> f = r.getFields(RpcInject.class);
        Set<Class<?>> beans = r.getBeans(RpcService.class);
        System.out.println(beans.size());
        System.out.println(f);
        System.out.println(f.size());
    }
}
