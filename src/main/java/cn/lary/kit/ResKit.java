package cn.lary.kit;

import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
    public static SingleResponse ok() {
        return ok(null);
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
    public static void responseFail(HttpServletResponse response,String message) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getWriter().write(JSONKit.toJSON(fail(message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void responseFail(HttpServletResponse response,ResultCode resultCode) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getWriter().write(JSONKit.toJSON(fail(resultCode)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
