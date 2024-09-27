package cn.lary.module.user.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBase {
    private Integer uid;
    private String name;
}
