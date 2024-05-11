package cn.lary.rpc.client;

public interface AsyncRpcCallback {
    void success(Object result);
    void fail(Exception e);
}
