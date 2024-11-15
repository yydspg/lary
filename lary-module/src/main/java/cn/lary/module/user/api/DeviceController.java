package cn.lary.module.user.api;

import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.module.user.component.DeviceExecute;
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

    private final DeviceExecute deviceExecute;

    @GetMapping("/my")
    public MultiResponse<DeviceVO> my() {
        ResponsePair<List<DeviceVO>> response = deviceExecute.my();
        if (response.isFail()) {
            return ResponseKit.multiFail(response.getMsg());
        }
        return ResponseKit.multiOk(response.getData());
    }

//    @GetMapping("/code")
//    public SingleResponse<Void> addDeviceCode(@RequestBody @Valid DeviceAddDTO dto) {
//        ResponsePair<Void> response = deviceExecute.addDeviceCode(dto);
//        if (response.isFail()) {
//            return ResponseKit.fail(response.getMsg());
//        }
//        return ResponseKit.ok();
//    }

    @GetMapping("/del")
    public SingleResponse<DeviceVO> remove(@RequestParam(value = "did") @NotNull Integer did) {
        ResponsePair<Void> response = deviceExecute.removeDevice( did);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

}