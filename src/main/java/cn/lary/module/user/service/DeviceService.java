package cn.lary.module.user.service;

import cn.lary.module.user.entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface DeviceService extends IService<Device> {
    Device queryDevice(String uid,String deviceId);
    void updateDeviceLogin(Device device);
    List<Device> queryDevicesWithUid(String uid);
    void deleteDevice(String deviceId,String uid);
}
