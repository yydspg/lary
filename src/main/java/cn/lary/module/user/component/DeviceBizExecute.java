package cn.lary.module.user.component;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.user.dto.DeviceAddDTO;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.vo.DeviceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceBizExecute {

    private final DeviceService deviceService;

    /**
     * 新设备登陆的验证码
     * @param dto {@link DeviceAddDTO}
     * @return ok
     */
    public ResponsePair<Void> addDeviceCode(DeviceAddDTO dto) {
        return deviceService.getAddDeviceSmsCode(dto);
    }


    /**
     * 查询所有设备
     * @return {@link DeviceVO}
     */
    public ResponsePair<List<DeviceVO>> my() {
        return deviceService.devices();
    }


    /**
     * 删除设备
     * @param deviceId d
     * @return v
     */
    public ResponsePair<Void> removeDevice( int deviceId) {
       return deviceService.removeDevice(deviceId);
    }



}
