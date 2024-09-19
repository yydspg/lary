package cn.lary.core.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MultiResponse<T> extends Response<T> {

    private Collection<T> data;


    public static <T> MultiResponse<T> buildSuccess(Collection<T> data) {
        MultiResponse<T> response = new MultiResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
    public static <T> MultiResponse<T> buildSuccess() {
        MultiResponse<T> response = new MultiResponse<T>();
        response.setSuccess(true);
        return response;
    }
    public static <T> MultiResponse<T> buildFailure(Integer errCode, String errMessage) {
        MultiResponse<T> response = new MultiResponse<T>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }


}

