package cn.lary.common.context;

import lombok.Data;

@Data
public  class Profile {
    /**
     * uid
     */
    private long uid;

    /**
     * username
     */
    private String name;

    /**
     * device flag
     */
    private int flag;
}
