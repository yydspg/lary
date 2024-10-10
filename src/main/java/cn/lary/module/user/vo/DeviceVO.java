package cn.lary.module.user.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeviceVO {

    private int id;
    private String deviceName;

    private Long lastLogin;

    private Boolean isLanding;
}
