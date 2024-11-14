package cn.lary.module.stream.api;

import cn.lary.module.stream.component.FanExecute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  粉丝api
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@RestController
@RequestMapping("/v1/record")
@RequiredArgsConstructor
public class FanController {

    private final FanExecute fanExecute;


//
//    @GetMapping("/redpacket/get")
//    public SingleResponse<RedpacketCacheDTO> getRedPacket(@RequestParam @NotNull Integer toUid) {
//        ResponsePair<RedpacketCacheDTO> response = fanExecute.getRedPacketInfo(toUid);
//        if (response.isFail()) {
//            return ResponseKit.FAIL(response.getMsg());
//        }
//        return ResponseKit.OK(response.getData());
//    }
//    @GetMapping("/redpacket/join")
//    public SingleResponse<Void> joinRedPacket(@RequestParam @NotNull Integer toUid) {
//        long uid = RequestContext.uid();
//        ResponsePair<Void> response = fanExecute.redWars(uid, toUid);
//        if (response.isFail()) {
//            return ResponseKit.FAIL(response.getMsg());
//        }
//        return ResponseKit.OK();
//    }
}
