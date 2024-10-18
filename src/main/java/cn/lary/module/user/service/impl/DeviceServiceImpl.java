package cn.lary.module.user.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.*;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.user.dto.DeviceAddDTO;
import cn.lary.module.user.dto.DeviceAddResponseCacheDTO;
import cn.lary.module.user.dto.DeviceLoginCacheDTO;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.mapper.DeviceMapper;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.vo.DeviceVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    private final RedisCache redisCache;
    private final RedisBizConfig redisBizConfig;
    private final KVBuilder kvBuilder;

    @Override
    public ResponsePair<Void> removeDevice(int deviceId) {
        Device device = lambdaQuery()
                .select(Device::getUid)
                .select(Device::getId)
                .eq(Device::getId, deviceId)
                .eq(Device::getUid, RequestContext.getLoginUID())
                .one();
        if (device == null || device.getIsDelete()) {
            return BizKit.ok();
        }
        lambdaUpdate()
                .set(Device::getIsDelete, true)
                .eq(Device::getId, deviceId)
                .eq(Device::getUid, RequestContext.getLoginUID());
        return BizKit.ok();
    }

    @Override
    public void removeDeviceLoginCache(int uid,int flag){
        redisCache.delete(kvBuilder.deviceLoginK(uid, flag));
    }

    @Override
    public void renewalDeviceLoginCache(int uid, int flag) {
        redisCache.renewal(kvBuilder.deviceLoginK(uid,flag),redisBizConfig.getLoginDeviceCacheExpire());
    }

    @Override
    public void buildDeviceLoginCache(int uid,int flag, DeviceLoginCacheDTO dto) {
        redisCache.setHash(kvBuilder.deviceLoginK(uid,flag),
                kvBuilder.deviceLoginV(dto),redisBizConfig.getLoginDeviceCacheExpire());
    }

    @Override
    public Device getDevice(int uid,int deviceId, String name, int flag) {
        Device device = lambdaQuery()
                .select(Device::getId)
                .eq(Device::getUid, uid)
                .eq(Device::getId, deviceId)
                .one();
        if (device != null) {
            return device;
        }
        // deep search
        List<Device> devices = lambdaQuery()
                .select(Device::getId)
                .select(Device::getUid)
                .select(Device::getName)
                .select(Device::getFlag)
                .eq(Device::getUid, RequestContext.getLoginUID())
                .list();
        if (CollectionKit.isEmpty(devices)) {
            return null;
        }
       return devices.stream()
               .filter(d -> StringKit.same(name, d.getName()) && flag == d.getFlag())
               .findFirst()
               .orElse(null);
    }

    @Override
    public ResponsePair<List<DeviceVO>> devices() {
        int uid = RequestContext.getLoginUID();
        List<Device> devices = lambdaQuery()
                .select(Device::getId, Device::getName)
                .select(Device::getFlag, Device::getLevel)
                .select(Device::getLastLogin)
                .eq(Device::getUid, uid)
                .list();
        if (CollectionKit.isEmpty(devices)) {
            log.error("no devices found,uid:{}",uid);
            return BizKit.fail("no devices found");
        }
        Map<Object, Object> pcLoginData = redisCache.getHash(kvBuilder.deviceLoginK(uid,LARY.DEVICE.FLAG.PC));
        Map<Object, Object> appLoginData = redisCache.getHash(kvBuilder.deviceLoginK(uid,LARY.DEVICE.FLAG.APP));
        if (pcLoginData == null && appLoginData == null) {
            log.error("no login devices found when search landing device,uid:{}",uid);
            return BizKit.fail("no login devices found");
        }
        DeviceLoginCacheDTO pcLoginDTO = null;
        DeviceLoginCacheDTO appLoginDTO= null;
        if (pcLoginData != null) {
            appLoginDTO = DeviceLoginCacheDTO.of(pcLoginData);
        }
        if (appLoginData != null) {
            pcLoginDTO = DeviceLoginCacheDTO.of(appLoginData);
        }
        List<DeviceVO> vos = new ArrayList<>();
        for (Device device : devices) {
            DeviceVO vo = new DeviceVO(device);
            if (vo.getFlag() == LARY.DEVICE.FLAG.APP
                && appLoginDTO != null
                && appLoginDTO.getId() == device.getId()){
                vo.setLanding(true);
            }
            if (vo.getFlag() == LARY.DEVICE.FLAG.PC
                && pcLoginDTO != null
                && pcLoginDTO.getId() == device.getId()){
                vo.setLanding(true);
            }
        }
        return BizKit.ok(vos);
    }

    @Override
    public ResponsePair<Void> getAddDeviceSmsCode(DeviceAddDTO dto) {
        String token = SmsCodeKit.getToken();
        DeviceAddResponseCacheDTO data = new DeviceAddResponseCacheDTO()
                .setCode(token)
                .setName(dto.getName())
                .setFlag(dto.getFlag());
        redisCache.setHash(kvBuilder.addDeviceK(dto.getUid(),dto.getPhone())
                ,kvBuilder.addDeviceV(data),redisBizConfig.getSmsAddDeviceExpire());
        return BizKit.ok();
    }

}
