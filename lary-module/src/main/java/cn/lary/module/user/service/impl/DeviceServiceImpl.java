package cn.lary.module.user.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.id.LaryIDBuilder;
import cn.lary.module.user.component.UserCache;
import cn.lary.module.user.component.UserCacheComponent;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.mapper.DeviceMapper;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.vo.DeviceVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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


    private final LaryIDBuilder builder;
    private final UserCacheComponent userCacheComponent;
    private final TransactionTemplate transactionTemplate;

    @Override
    public final Device build(Device dto) {
        dto.setDid(builder.next());
        save(dto);
        return dto;
    }

    @Override
    public ResponsePair<Void> removeDevice(int did) {
        return transactionTemplate.execute(status -> {
            Device device = lambdaQuery()
                    .select(Device::getUid,Device::getDid)
                    .eq(Device::getDid, did)
                    .eq(Device::getUid, RequestContext.uid())
                    .one();
            if (device == null || device.getStatus() == LARY.CORE.STATUS.REMOVE) {
                return BusinessKit.ok();
            }
            lambdaUpdate()
                    .set(Device::getStatus, LARY.CORE.STATUS.REMOVE)
                    .eq(Device::getId, did)
                    .eq(Device::getUid, RequestContext.uid())
                    .update();
            return BusinessKit.ok();
        });
    }


    @Override
    public Device getDevice(long deviceId, String name, int flag) {
        long uid = RequestContext.uid();
        Device device = lambdaQuery()
                .select(Device::getUid)
                .eq(Device::getId, deviceId)
                .one();

        if (device != null && device.getUid() == uid) {
            return device;
        }
        if (device != null) {
            return null;
        }
        // deep search
        List<Device> devices = lambdaQuery()
                .select(Device::getId,Device::getName,
                        Device::getFlag,Device::getUid)
                .eq(Device::getUid, RequestContext.uid())
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
        long uid = RequestContext.uid();
        List<Device> devices = lambdaQuery()
                .select(Device::getId, Device::getName,
                        Device::getFlag, Device::getLevel,
                        Device::getLastLogin)
                .eq(Device::getUid, uid)
                .list();
        if (CollectionKit.isEmpty(devices)) {
            log.error("no devices found,uid:{}",uid);
            return BusinessKit.fail("no devices found");
        }
        UserCache pcLoginData = userCacheComponent.getData(uid, LARY.DEVICE.FLAG.PC);
        UserCache appLoginData = userCacheComponent.getData(uid, LARY.DEVICE.FLAG.APP);
        if (pcLoginData == null && appLoginData == null) {
            log.error("no login devices found when search landing device,uid:{}",uid);
            return BusinessKit.fail("no login devices found");
        }
        long pcDid;
        long appDid;
        if (pcLoginData != null) {
            pcDid = pcLoginData.getDid();
        } else {
            pcDid = 0;
        }
        if (appLoginData != null) {
            appDid = appLoginData.getDid();
        } else {
            appDid = 0;
        }
        return BusinessKit.ok( devices.stream().map(d->{
            DeviceVO vo = new DeviceVO(d);
            if (vo.getFlag() == LARY.DEVICE.FLAG.APP
                    && appDid == d.getId()){
                vo.setLanding(true);
            }
            if (vo.getFlag() == LARY.DEVICE.FLAG.PC
                    && pcDid == d.getId()){
                vo.setLanding(true);
            }
            return vo;
        }).toList());
    }

//    @Override
//    public ResponsePair<Void> getAddDeviceSmsCode(DeviceAddDTO dto) {
//        String srsToken = SmsCodeKit.getSrsToken();
//        DeviceAddResponseCacheDTO data = new DeviceAddResponseCacheDTO()
//                .setCode(srsToken)
//                .setName(dto.getName())
//                .setFlag(dto.getFlag());
//        cacheComponent.setHash(kvBuilder.addDeviceK(dto.getUid(),dto.getPhone())
//                ,kvBuilder.addDeviceV(data), redisBusinessConfig.getSmsAddDeviceExpire());
//        return BusinessKit.OK();
//    }

}
