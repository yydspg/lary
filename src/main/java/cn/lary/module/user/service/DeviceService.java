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
    Device queryDevice(int uid,int deviceId);
    List<Device> queryDevicesWithUid(int uid);
    Device checkWhetherNewDevice(int uid,Integer deviceId,String deviceName,String deviceModel);
}
