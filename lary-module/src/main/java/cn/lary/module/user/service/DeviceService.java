package cn.lary.module.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.user.dto.DeviceAddDTO;
import cn.lary.module.cache.dto.DeviceLoginCacheDTO;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.vo.DeviceVO;
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

    /**
     * build
     * @param dto {@link Device}
     * @return OK
     */
    Device build(Device dto);

    /**
     * 删除已授权的设备<br>
     * 登陆的设备不能删除<br>
     * 主设备不能删除
     * @param deviceId d
     * @return OK
     */
    ResponsePair<Void> removeDevice( int deviceId);

//    /**
//     * 删除设备登录的token
//     * @param  flag 设备flag
//     */
//    void removeDeviceLoginCache(long uid,int flag);
//
//    /**
//     *刷新token
//     */
//    void renewalDeviceLoginCache(long uid,int flag);
//
//
//    /**
//     * 添加设备登陆的token
//     * @param flag 设备flag
//     * @param dto {@link DeviceLoginCacheDTO}
//     */
//    void buildDeviceLoginCache(long uid,int flag, DeviceLoginCacheDTO dto);

    /**
     * 获取设备
     * @param deviceId d
     * @param name 设备名称
     * @param flag 设备模式
     * @return OK
     */
    Device getDevice(long deviceId,String name,int flag);

    /**
     * 查询登陆设备
     * @return {@link DeviceVO}
     */
    ResponsePair<List<DeviceVO>> devices();

//    /**
//     * 新设备登陆验证接口
//     * @param dto {@link DeviceAddDTO}
//     * @return OK
//     */
//    ResponsePair<Void> getAddDeviceSmsCode(DeviceAddDTO dto);

}
