package cn.lary.module.user.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.JSONKit;
import cn.lary.kit.ResKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.user.core.DeviceBizExecute;
import cn.lary.module.user.dto.DeviceRegisterTokenReq;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.vo.DeviceVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private final DeviceBizExecute deviceBizExecute;

    @GetMapping("/ack{code}")
    public SingleResponse<Void> ackAddDevice(@PathVariable @NotNull String code) {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = deviceBizExecute.ackAddDeviceCMD(uid, code);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

    @GetMapping("/list")
    public SingleResponse<List<DeviceVO>> list() {
        String uid = ReqContext.getLoginUID();
        ResPair<List<DeviceVO>> res = deviceBizExecute.list(uid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

    @GetMapping("/del/token{deviceId}")
    public SingleResponse<Void> delToken(@PathVariable @NotNull String deviceId) {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = deviceBizExecute.delDeviceToken(uid, deviceId);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

    @GetMapping("/del/device{deviceId}")
    public SingleResponse<DeviceVO> delDevice(@PathVariable @NotNull String deviceId) {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = deviceBizExecute.removeAuthedDevice(uid, deviceId);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
}
