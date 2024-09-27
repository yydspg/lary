package cn.lary.module.user.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.user.core.DeviceBizExecute;
import cn.lary.module.user.vo.DeviceVO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceBizExecute deviceBizExecute;

    @GetMapping("/ack")
    public SingleResponse<Void> ackAddDevice(@RequestParam(value = "code") @NotNull String code) {
        Integer uid = ReqContext.getLoginUID();
        ResPair<Void> res = deviceBizExecute.ackAddDeviceCMD(uid, code);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

    @GetMapping("/list")
    public SingleResponse<List<DeviceVO>> list() {
        Integer uid = ReqContext.getLoginUID();
        ResPair<List<DeviceVO>> res = deviceBizExecute.list(uid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

    @GetMapping("/del/token")
    public SingleResponse<Void> delToken(@RequestParam(value = "deviceId") @NotNull String deviceId) {
        Integer uid = ReqContext.getLoginUID();
        ResPair<Void> res = deviceBizExecute.delDeviceToken(uid, deviceId);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }

    @GetMapping("/del")
    public SingleResponse<DeviceVO> delDevice(@RequestParam(value = "deviceId") @NotNull String deviceId) {
        Integer uid = ReqContext.getLoginUID();
        ResPair<Void> res = deviceBizExecute.removeAuthedDevice(uid, deviceId);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
}
