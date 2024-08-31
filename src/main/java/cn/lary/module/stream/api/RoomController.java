package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.stream.dto.req.StartStreamReq;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@RestController
@RequestMapping("/v1/stream/room")
@RequiredArgsConstructor
public class RoomController {
    private final DeviceService deviceService;
    @PostMapping("/start")
    public SingleResponse start(@RequestBody @Valid StartStreamReq req) {

        String uid = ReqContext.getLoginUID();
        String deviceId = req.getDeviceId();
        Device device = deviceService.queryDevice(deviceId, uid);
        if (device == null) {
            return ResKit.fail("no such device");
        }

        return null;
    }
}
