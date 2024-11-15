package cn.lary.common.context;

import lombok.Data;

@Data
public  class Profile {

    private long uid;

    private String name;

    /**
     * device flag
     */
    private int flag;
}
