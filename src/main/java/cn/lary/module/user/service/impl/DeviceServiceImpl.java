package cn.lary.module.user.service.impl;

import cn.lary.kit.CollectionKit;
import cn.lary.kit.StringKit;
import cn.lary.kit.SystemKit;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.mapper.DeviceMapper;
import cn.lary.module.user.service.DeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    private final DeviceMapper deviceMapper;
    @Override
    public Device queryDevice(int uid, int deviceId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getUid,uid).eq(Device::getId,deviceId)
                .eq(Device::getIsDelete,false),false);
    }



    @Override
    public List<Device> queryDevicesWithUid(int uid) {
        LambdaQueryWrapper<Device> qw = new LambdaQueryWrapper<Device>()
                .eq(Device::getUid, uid)
                .eq(Device::getIsDelete,false);
        return baseMapper.selectList(qw);
    }

    @Override
    public Device checkWhetherNewDevice(int uid, Integer deviceId, String deviceName, String deviceModel) {
        Device device = null;
        if (deviceId != null) {
            device = deviceMapper.selectById(deviceId);
        }else {
            List<Device> devices = deviceMapper.selectList(new LambdaQueryWrapper<Device>()
                    .eq(Device::getUid, uid)
                    .eq(Device::getIsDelete, false));
            List<Device> matchDevice = devices.stream().filter(d -> StringKit.same(d.getName(), deviceName)
                    && StringKit.same(d.getModel(), deviceModel)).toList();
            if (CollectionKit.isNotEmpty(matchDevice)) {
                device = matchDevice.get(0);
            }
        }
        return device;
    }

}
