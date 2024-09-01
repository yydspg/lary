package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.danmaku.service.DanmakuService;
import cn.lary.module.stream.dto.req.StartStreamReq;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.service.DeviceService;
import cn.lary.pkg.srs.config.SrsConfig;
import cn.lary.pkg.wk.entity.core.Channel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    private final RoomService roomService;
    private final DanmakuService danmakuService;
    private final SrsConfig srsConfig;

    @PostMapping("/start")
    public SingleResponse start(@RequestBody @Valid StartStreamReq req) {

        String uid = ReqContext.getLoginUID();
        String deviceId = req.getDeviceId();
        //query device
        Device device = deviceService.queryDevice(deviceId, uid);
        if (device == null) {
            return ResKit.fail("no such device");
        }
        // check if is first start stream
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid));

        if (room == null) {
            // first time to start stream
            // build danmaku channel
            Channel danmakuChannel = danmakuService.getDanmakuChannel(uid);
            if (danmakuChannel == null) {
                return ResKit.fail("danmaku channel not available");
            }

            room = new Room().setUid(uid).setChannelId(danmakuChannel.getChanelID()).setIsAlive(true).setIsHot(false)
                    .setScore(0L);

        }
        return null;
    }
}
