package com.lary.common.core.enums;

/**
 * @author paul 2023/12/15
 */

public enum ClientTypeEnum {
    /**
     * 移动端
     */
    H5("mobile_end"),
    /**
     * pc端
     */
    PC("computer_terminal"),
    /**
     * 小程序端
     */
    WECHAT_MP("mini_program_side"),
    /**
     * 移动应用端
     */
    APP("application_end"),
    /**
     * 未知
     */
    UNKNOWN("unknown info");
    private final String clientName;
    ClientTypeEnum(String des){
        this.clientName = des;
    }
    public String getClientName(){
        return this.clientName;
    }
    public String value(){
        return this.name();
    }
}
