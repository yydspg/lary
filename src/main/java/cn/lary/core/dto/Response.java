package cn.lary.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class Response<T> extends DTO{
    private boolean success;

    private int errCode;

    private String errMessage;


    @Override
    public String toString() {
        return "Response [success=" + success + ", errCode=" + errCode + ", errMessage=" + errMessage + "]";
    }

}
