package cn.lary.module.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RegisterVO {

    private Integer uid;

    private String name;

    private String avatarUrl;

    private String role;

    private Integer status;
    /**
     * 0 is man,1 is woman
     */
    private Integer sex;

//    private String username;

    private String password;

    private String zone;

    private String phone;

    private String regin;

    private Boolean isRobot;

    private String vercode;

    private String qrVercode;

    private Integer level;
}
