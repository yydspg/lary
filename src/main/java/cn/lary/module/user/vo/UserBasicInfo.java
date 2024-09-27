package cn.lary.module.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBasicInfo {
    private Integer uid;
    private String name;
    private Boolean isRobot;
    private String vercode;
}
