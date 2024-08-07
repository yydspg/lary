package cn.lary.module.user.serviceImpl;

import cn.lary.kit.SystemKit;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.mapper.DeviceMapper;
import cn.lary.module.user.service.DeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Override
    public Device queryDevice(String deviceId, String uid) {
        LambdaQueryWrapper<Device> qw = new LambdaQueryWrapper<>();
        qw.eq(Device::getDeviceId, deviceId);
        qw.eq(Device::getUid, uid);
        return baseMapper.selectOne(qw);
    }

    @Override
    public void updateDeviceLogin(Device device) {
        device.setLastLogin(SystemKit.now());
        baseMapper.updateById(device);
    }
}
