package com.lary.common.core.utils;

import com.lary.common.core.response.ResponseEnum;
import com.lary.common.core.response.ResponseMsg;

/**
 * @author paul 2024/1/1
 */

public class ResponseUtil<T>{

    private final ResponseMsg<T> res;
    private static final Integer SUCCESS = 200;
    public ResponseUtil(){
        res = new ResponseMsg<>();
        res.setSuccess(true);
        res.setMsg("success");
        res.setCode(SUCCESS);
    }
    public ResponseMsg<T> setData(T t){
        this.res.setRes(t);
        return this.res;
    }
    public ResponseMsg<T> setSuccessMsg(ResponseEnum resCode){
        res.setSuccess(true);
        res.setMsg(resCode.getDes());
        res.setCode(resCode.getCode());
        return res;
    }
    public ResponseMsg<T> setErrorMsg(ResponseEnum resCode){
        res.setSuccess(false);
        res.setMsg(resCode.getDes());
        res.setCode(resCode.getCode());
        return res;
    }
    public static <T> ResponseMsg<T> data(T t){return new ResponseUtil<T>().setData(t);}
    public static <T> ResponseMsg<T> success(ResponseEnum resultCode) {
        return new ResponseUtil<T>().setSuccessMsg(resultCode);
    }
    public static <T> ResponseMsg<T> error(ResponseEnum resultCode) {
        return new ResponseUtil<T>().setErrorMsg(resultCode);
    }
}
