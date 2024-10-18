package cn.lary.module.user.api;

import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.user.dto.DeviceAddDTO;
import cn.lary.module.user.execute.DeviceBizExecute;
import cn.lary.module.user.vo.DeviceVO;
import jakarta.validation.Valid;
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


    @GetMapping("/my")
    public MultiResponse<DeviceVO> my() {
        ResponsePair<List<DeviceVO>> response = deviceBizExecute.my();
        if (response.isFail()) {
            return ResponseKit.multiFail(response.getMsg());
        }
        return ResponseKit.multiOk(response.getData());
    }

    @GetMapping("/code")
    public SingleResponse<Void> addDeviceCode(@RequestBody @Valid DeviceAddDTO dto) {
        ResponsePair<Void> response = deviceBizExecute.addDeviceCode(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/del")
    public SingleResponse<DeviceVO> delDevice(@RequestParam(value = "deviceId") @NotNull Integer deviceId) {
        ResponsePair<Void> response = deviceBizExecute.removeDevice( deviceId);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
}
