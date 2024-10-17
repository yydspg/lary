package cn.lary.module.user.api;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.user.execute.DeviceBizExecute;
import cn.lary.module.user.vo.DeviceVO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceBizExecute deviceBizExecute;

    @GetMapping("/ack")
    public SingleResponse<Void> ackAddDevice(@RequestParam(value = "code") @NotNull String code) {
        int uid = RequestContext.getLoginUID();
        ResponsePair<Void> res = deviceBizExecute.responseAddDeviceCMD(uid, code);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    @GetMapping("/list")
    public SingleResponse<List<DeviceVO>> list() {
        int uid = RequestContext.getLoginUID();
        ResponsePair<List<DeviceVO>> res = deviceBizExecute.list(uid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    @GetMapping("/del/token")
    public SingleResponse<Void> delToken(@RequestParam(value = "deviceId") @NotNull Integer deviceId) {
        int uid = RequestContext.getLoginUID();
        ResponsePair<Void> res = deviceBizExecute.delDeviceToken(uid, deviceId);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    @GetMapping("/del")
    public SingleResponse<DeviceVO> delDevice(@RequestParam(value = "deviceId") @NotNull Integer deviceId) {
        int uid = RequestContext.getLoginUID();
        ResponsePair<Void> res = deviceBizExecute.removeDevice(uid, deviceId);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
}
