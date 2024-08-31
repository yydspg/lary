package cn.lary.module.user.dto.res;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBaseRes {
    private String uid;
    private String name;
    private Boolean deleted;
    private Boolean isRobot;
}
