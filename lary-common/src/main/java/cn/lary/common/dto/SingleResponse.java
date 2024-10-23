package cn.lary.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResponse<T> extends Response<T> {

    private T data;


    public static <T> SingleResponse<T> buildSuccess(T data) {
        SingleResponse<T> response = new SingleResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    public static <T> SingleResponse<T> buildFail(int errorCode,String errMessage) {
        SingleResponse<T> response = new SingleResponse<T>();
        response.setSuccess(false);
        response.setErrCode(errorCode);
        response.setErrMessage(errMessage);
        return response;
    }
    public static <T> SingleResponse<T> buildFail(String errorMsg) {
        SingleResponse<T> response = new SingleResponse<T>();
        response.setSuccess(false);
        response.setErrCode(9001);
        response.setErrMessage(errorMsg);
        return response;
    }
}