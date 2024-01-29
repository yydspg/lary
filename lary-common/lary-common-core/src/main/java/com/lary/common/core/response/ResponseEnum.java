package com.lary.common.core.response;

import lombok.Data;

/**
 * @author paul 2024/1/1
 */
@Data
public class ResponseEnum {
    private final int code;

    private final String des;

    public int value() {
        return code;
    }

    public String getMsg() {
        return des;
    }

    ResponseEnum(int code, String des) {
        this.code = code;
        this.des = des;
    }

    @Override
    public String toString() {
        return "ResponseEnum{" + "code='" + code + '\'' + ", des='" + des + '\'' + "} " + super.toString();
    }
}
