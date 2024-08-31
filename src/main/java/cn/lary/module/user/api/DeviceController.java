package cn.lary.module.user.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.JSONKit;
import cn.lary.kit.ResKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.user.dto.req.DeviceRegisterTokenReq;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;

    @GetMapping("/list")
    public MultiResponse getAllDevices() {
        String uid = ReqContext.getLoginUID();
        List<Device> devices = deviceService.queryDevicesWithUid(uid);
        if (CollectionKit.isNotEmpty(devices)) {
            return ResKit.multiFail("no device found");
        }
        Device currentDevice = devices.get(0);
        currentDevice.setDeviceName("current device" + currentDevice.getDeviceName());
        return ResKit.multiOk(devices);
    }
    @GetMapping("/del")
    public SingleResponse delDevice(@RequestParam(value = "device_id" ) @NotBlank String deviceId) {
        String uid = ReqContext.getLoginUID();
        deviceService.deleteDevice(uid, deviceId);
        return ResKit.ok();
    }

    /**
     * register device to redis
     * @param req {@link DeviceRegisterTokenReq}
     * @return ok
     */
    @PostMapping("/token")
    public SingleResponse registerDeviceToken(@RequestBody @Valid DeviceRegisterTokenReq req) {
        String uid = ReqContext.getLoginUID();
        String K = kvBuilder.buildDeviceLoginTokenKey(uid);
        HashMap<String, String> map = new HashMap<>();
        map.put("device_token", req.getDeviceToken());
        map.put("device_type", req.getDeviceType());
        map.put("bundle_id", req.getBundleId());
        String V = JSONKit.toJSON(map);
        redisCache.set(K,V);
        return ResKit.ok();
    }

    /**
     * del redis cache about device
     * @return ok
     */
    @GetMapping("/del/token")
    public SingleResponse delRegisterDeviceToken() {
        String uid = ReqContext.getLoginUID();
        String K = kvBuilder.buildDeviceLoginTokenKey(uid);
        redisCache.del(K);
        return ResKit.ok();
    }
}
