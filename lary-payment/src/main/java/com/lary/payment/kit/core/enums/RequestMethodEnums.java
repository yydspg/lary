package com.lary.payment.kit.core.enums;

/**
 * @author paul 2024/1/29
 */

public enum RequestMethodEnums {
    /**
     * 上传实质是 post 请求
     */
    UPLOAD("POST"),
    /**
     * post 请求
     */
    POST("POST"),
    /**
     * get 请求
     */
    GET("GET"),
    /**
     * put 请求
     */
    PUT("PUT"),
    /**
     * delete 请求
     */
    DELETE("DELETE"),
    /**
     * options 请求
     */
    OPTIONS("OPTIONS"),
    /**
     * head 请求
     */
    HEAD("HEAD"),
    /**
     * trace 请求
     */
    TRACE("TRACE"),
    /**
     * connect 请求
     */
    CONNECT("CONNECT");
    private final String methodType;
    RequestMethodEnums(String methodType){
        this.methodType = methodType;
    }
}
