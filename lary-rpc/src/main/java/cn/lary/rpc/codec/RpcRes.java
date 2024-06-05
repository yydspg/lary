package cn.lary.rpc.codec;

import java.io.Serial;
import java.io.Serializable;

public  class RpcRes implements Serializable {
    @Serial
    private static final long   serialVersionUID = -4364536436151723421L;

    private String reqId;
    private String errorMsg ;
    private Object appRes;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getAppRes() {
        return appRes;
    }

    public void setAppRes(Object appRes) {
        this.appRes = appRes;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }
    public boolean isError() {
        return errorMsg != null;
    }
}
