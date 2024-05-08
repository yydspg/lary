package cn.lary.rpc.registry;

import java.util.Map;

public interface RegistryClient {
    // host means instance ip,port means instance port
    public void login(String instanceHost, int instancePort,String name,String version);

    public void logout(String instanceHost,int instancePort,String name,String version);
}
