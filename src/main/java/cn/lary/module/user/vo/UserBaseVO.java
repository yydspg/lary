package cn.lary.module.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBaseVO {
    private Integer uid;
    private String name;
    private Boolean isRobot;
}
