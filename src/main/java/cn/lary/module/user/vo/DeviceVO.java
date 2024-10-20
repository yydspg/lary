package cn.lary.module.user.vo;

import cn.lary.module.user.entity.Device;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeviceVO {

    private int id;

    private String deviceName;

    private long lastLogin;

    private boolean landing;

    private int level;

    private int flag;

    public DeviceVO(Device device) {
        this.id = device.getId();
        this.deviceName = device.getName();
        this.lastLogin = device.getLastLogin();
        this.level = device.getLevel();
        this.flag = device.getFlag();
    }
}
