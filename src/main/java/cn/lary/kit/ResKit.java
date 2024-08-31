package cn.lary.kit;

import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;

import java.util.Collection;

public class ResKit {
    public static SingleResponse fail(ResultCode resultCode) {
       return  SingleResponse.buildFailure(resultCode.code(), resultCode.message());
    }
    public static SingleResponse fail(String message) {
        return SingleResponse.buildFailure("9001",message);
    }
    public static SingleResponse ok(Object data) {
        return SingleResponse.buildSuccess(data);
    }
    public static MultiResponse multiOk() {
        return MultiResponse.buildSuccess();
    }
    public static MultiResponse multiOk(Collection<?> data) {
        return MultiResponse.buildSuccess(data);
    }
    public static MultiResponse multiFail(String message) {
        return MultiResponse.buildFailure("9001",message);
    }
}
