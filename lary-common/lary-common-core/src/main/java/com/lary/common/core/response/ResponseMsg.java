package com.lary.common.core.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author paul 2024/1/1
 */

@Data
public class ResponseMsg<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    private boolean success;

    /**
     * 消息
     */
    private String msg;

    /**
     * 返回代码
     */
    private Integer code;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 结果对象
     */
    private T res;
}