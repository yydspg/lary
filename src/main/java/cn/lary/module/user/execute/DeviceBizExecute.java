package cn.lary.module.user.execute;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.*;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.user.dto.DeviceAddAckCacheDTO;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.vo.DeviceVO;
import cn.lary.pkg.wk.constant.WK;
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
    private final RedisBizConfig redisBizConfig;

    /**
     * 响应 添加新设备
     * @param uid u
     * @param code 验证码
     * @return ok
     */
    public ResPair<Void> responseAddDeviceCMD(int uid,String code) {
        Map<Object, Object> data = redisCache.getHash(kvBuilder.addDeviceK(uid));
        if (data == null) {
            return BizKit.fail("no add device redis cache data");
        }
        DeviceAddAckCacheDTO dto = DeviceAddAckCacheDTO.of(data);

        if (StringKit.diff(code,dto.getCode())) {
            return BizKit.fail("auth code error");
        }
        Device device = new Device()
                .setModel(dto.getModel())
                .setUid(uid)
                .setName(dto.getName());
        deviceService.save(device);
        redisCache.del(kvBuilder.addDeviceK(uid));
        return BizKit.ok();
    }

    /**
     * 存在主设备,添加新设备<br>
     * 如果为新设备,发送验证码
     * @param uid u
     * @param name n
     * @param model m
     * @return {@link DeviceVO}
     */
    public ResPair<DeviceVO> addDevice(int uid,String name,String model,int flag) {
        if (flag == WK.DeviceLevel.master){
            return BizKit.fail("master device no support");
        }
        Device device = deviceService.checkWhetherNewDevice(uid, null, name, model);
        if (device == null) {
            // build verify code
            String token = SmsCodeKit.getToken();
            DeviceAddAckCacheDTO data = new DeviceAddAckCacheDTO()
                    .setCode(token)
                    .setName(name)
                    .setModel(model);
            redisCache.setHash(kvBuilder.addDeviceK(uid),kvBuilder.addDeviceV(data),redisBizConfig.getSmsAddDeviceExpire());
            return BizKit.ok();
        }
        DeviceVO vo = new DeviceVO()
                .setDeviceName(device.getName())
                .setLastLogin(device.getLastLogin())
                .setId(device.getId());
        return BizKit.ok(vo);
    }
    /**
     * 查询所有设备
     * @param uid u
     * @return {@link DeviceVO}
     */
    public ResPair<List<DeviceVO>> list(int uid) {
        List<Device> devices = deviceService.queryDevicesWithUid(uid);
        if (CollectionKit.isEmpty(devices)) {
            log.error("device list fail,uid:{}",uid);
            return BizKit.fail("no device found");
        }
        List<DeviceVO> deviceVOs = new ArrayList<>();
        devices.forEach(device -> {
            DeviceVO deviceVO = new DeviceVO()
                    .setDeviceName(device.getName())
                    .setLastLogin(device.getLastLogin());
            String token = redisCache.get(kvBuilder.deviceLoginK(uid, device.getId()));
            if (StringKit.isNotEmpty(token)) {
                deviceVO.setIsLanding(true);
            }
            deviceVOs.add(deviceVO);
        });
        return BizKit.ok(deviceVOs);
    }

    /**
     * 删除设备登陆的token
     * @param uid u
     * @param deviceId d
     * @return void
     */
    public ResPair<Void> delDeviceToken(int uid,int deviceId) {
        redisCache.del(kvBuilder.deviceLoginK(uid,deviceId));
        return BizKit.ok();
    }

    /**
     * 删除已授权的设备<br>
     * 登陆的设备不能删除<br>
     * 主设备不能删除
     * @param uid u
     * @param deviceId d
     * @return v
     */
    public ResPair<Void> removeDevice(int uid,int deviceId) {
        //
        String token = redisCache.get(kvBuilder.deviceLoginK(uid, deviceId));
        if (StringKit.isNotEmpty(token)) {
            return BizKit.fail("device landing,can not be deleted");
        }
        // check login status
        Device device = deviceService.queryDevice(uid, deviceId);
        if (device == null || device.getLevel() == WK.DeviceLevel.master) {
            return BizKit.fail("main device can not be deleted");
        }
        deviceService.lambdaUpdate()
                .eq(Device::getId, deviceId)
                .eq(Device::getUid,uid)
                .set(Device::getIsDelete,true);
        return BizKit.ok();
    }


}
