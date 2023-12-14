package com.lary.common.core.enums;

/**
 * switch status enum
 * @author paul 2023/12/15
 */

public enum SwitchEnum {
    OPEN("opened_status"),
    CLOSE("closed_status");
    private final String description;
    SwitchEnum(String des){
        this.description = des;
    }
    public String getDescription(){
        return description;
    }
}
