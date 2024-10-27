package cn.lary.module.stream.api;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.module.stream.component.FanExecute;
import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.dto.RedPacketCacheDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


    @GetMapping("/raffle/join")
    public SingleResponse<Void> joinRaffle(@RequestParam @NotNull Integer toUid) {
        long uid = RequestContext.getLoginUID();
        ResponsePair<Void> response = fanExecute.joinRaffle(uid, toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/raffle/get")
    public SingleResponse<RaffleCacheDTO> getRaffle(@RequestParam @NotNull Integer toUid) {
        ResponsePair<RaffleCacheDTO> response = fanExecute.getRaffleInfo(toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    @GetMapping("/redpacket/get")
    public SingleResponse<RedPacketCacheDTO> getRedPacket(@RequestParam @NotNull Integer toUid) {
        ResponsePair<RedPacketCacheDTO> response = fanExecute.getRedPacketInfo(toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }
    @GetMapping("/redpacket/join")
    public SingleResponse<Void> joinRedPacket(@RequestParam @NotNull Integer toUid) {
        long uid = RequestContext.getLoginUID();
        ResponsePair<Void> response = fanExecute.redWars(uid, toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }
}
