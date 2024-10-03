package cn.lary.kit;

import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.SingleResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class ResKit {

    /*
    单数据返回
     */
    public static <T> SingleResponse<T> fail(ResultCode resultCode) {
       return  SingleResponse.buildFail(resultCode.code(),resultCode.message());
    }
    public static <T> SingleResponse<T> fail(String message) {
        return SingleResponse.buildFail(9001,message);
    }

    public static <T> SingleResponse<T> ok(T data) {
        return SingleResponse.buildSuccess(data);
    }

    public static <T> SingleResponse<T> ok() {
        return ok(null);
    }

    /*
    list数据返沪接口
     */
    public static <T> MultiResponse<T> multiOk() {
        return MultiResponse.buildSuccess();
    }

    public static <T> MultiResponse<T> multiOk(Collection<T> data) {
        return MultiResponse.buildSuccess(data);
    }

    public static <T> MultiResponse<T> multiFail(String message) {
        return MultiResponse.buildFailure(9001,message);
    }

    /*
    分页返回
     */
    public static <T> PageResponse<T> pageOk(Collection<T> data, long current,long size) {
        return PageResponse.ok(current,size,data);
    }
    public static <T> PageResponse<T> pageFail(String message) {
        return PageResponse.fail(9001,message);
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
