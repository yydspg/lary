package cn.lary.module.user.vo;

import cn.lary.module.user.entity.LoginLog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class LoginVO {
    /**
     * 登录ip
     */
    private String ip;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备模型
     */
    private int flag;

    private int level;

    /**
     * 创建时间
     */
    private long createAt;


    public LoginVO(LoginLog log) {
        this.ip = log.getIp();
        this.name = log.getName();
        this.flag = log.getFlag();
        this.level = log.getLevel();
        this.createAt = log.getCreateAt();
    }
}
