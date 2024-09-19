package cn.lary.module.user.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.*;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.vo.DeviceVO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceBizExecute {
    private final DeviceService deviceService;
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;

    /**
     * 响应 添加新设备
     * @param uid u
     * @param code 验证码
     * @return ok
     */
    public ResPair<Void> ackAddDeviceCMD(String uid,String code) {
        String deviceLoginData = redisCache.get(kvBuilder.addDeviceK(uid));
        Map<String, String> map = JSONKit.toMap(deviceLoginData);
        String deviceModel = map.get("device_model");
        String verifyCode = map.get("verify_code");
        String deviceName = map.get("device_name");
        if (StringKit.diff(code,verifyCode)) {
            return BizKit.fail("auth code error");
        }
        String deviceId = UUIDKit.getUUID();
        Device device = new Device().setDeviceModel(deviceModel).setUid(uid).setDeviceName(deviceName).setDeviceId(deviceId);
        deviceService.save(device);
        //remove from redis
        redisCache.del(kvBuilder.addDeviceK(uid));
        return BizKit.ok();
    }

    /**
     * 查询所有设备
     * @param uid u
     * @return {@link DeviceVO}
     */
    public ResPair<List<DeviceVO>> list(String uid) {
        List<Device> devices = deviceService.queryDevicesWithUid(uid);
        if (CollectionKit.isNotEmpty(devices)) {
            log.error("device list fail,uid:{}",uid);
            return BizKit.fail("no device found");
        }
        List<DeviceVO> deviceVOs = new ArrayList<>();
        devices.forEach(device -> {
            DeviceVO deviceVO = new DeviceVO().setDeviceName(device.getDeviceName()).setLastLogin(device.getLastLogin());
            String token = redisCache.get(kvBuilder.deviceLoginK(uid, device.getDeviceId()));
            if (StringKit.isNotEmpty(token)) {
                deviceVO.setIsLanding(true);
            }
            deviceVOs.add(deviceVO);
        });
        return BizKit.ok(deviceVOs);
    }

    /**
     * 删除设备登陆 token
     * @param uid u
     * @param deviceId d
     * @return void
     */
    public ResPair<Void> delDeviceToken(String uid,String deviceId) {
        redisCache.del(kvBuilder.deviceLoginK(uid,deviceId));
        return BizKit.ok();
    }

    /**
     * 删除已授权的设备，登陆的设备不能删除，主设备不能删除
     * @param uid u
     * @param deviceId d
     * @return v
     */
    public ResPair<Void> removeAuthedDevice(String uid,String deviceId) {
        // already login
        String token = redisCache.get(kvBuilder.deviceLoginK(uid, deviceId));
        if (StringKit.isNotEmpty(token)) {
            return BizKit.fail("device landing,can not be deleted");
        }
        // check login status
        Device device = deviceService.queryDevice(uid, deviceId);
        if (device == null || device.getDeviceLevel() == WK.DeviceLevel.master) {
            return BizKit.fail("main device can not be deleted");
        }
        deviceService.update(new LambdaUpdateWrapper<Device>().eq(Device::getDeviceId, deviceId).eq(Device::getUid,uid)
                .set(Device::getIsDelete,true));
        return BizKit.ok();
    }
}
