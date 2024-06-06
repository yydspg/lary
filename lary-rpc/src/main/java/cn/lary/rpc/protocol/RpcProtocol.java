package cn.lary.rpc.protocol;

import java.util.ArrayList;
import java.util.List;

public class RpcProtocol {
    private String host;
    private int port;
    private List<RpcServiceInfo> rpcServiceInfos;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RpcServiceInfo> getRpcServiceInfos() {
        return rpcServiceInfos;
    }

    public void setRpcServiceInfos(List<RpcServiceInfo> rpcServiceInfos) {
        this.rpcServiceInfos = rpcServiceInfos;
    }
    public void addRpcServiceInfo(RpcServiceInfo rpcServiceInfo) {
        if (rpcServiceInfos == null) {
            rpcServiceInfos = new ArrayList<>();
        }
        if(!rpcServiceInfos.contains(rpcServiceInfo)){
            rpcServiceInfos.add(rpcServiceInfo);
        }
    }
}
