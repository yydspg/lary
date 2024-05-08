package cn.lary.rpc.core;

import cn.lary.rpc.codec.RpcReq;

public final class Beat {
    public static final int BEAT_INTERVAL = 30;
    public static final int BEAT_TIMEOUT = 3 * BEAT_INTERVAL;
    public static final String BEAT_ID = "BEAT_PING_PONG";

    public static RpcReq BEAT_PING;

    static {
        BEAT_PING = new RpcReq() {};
        BEAT_PING.setReqId(BEAT_ID);
    }
}
