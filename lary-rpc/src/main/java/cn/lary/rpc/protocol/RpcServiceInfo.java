package cn.lary.rpc.protocol;

import java.util.Map;

public class RpcServiceInfo {
    private String serviceName;
    private String version;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
