package cn.lary.module.stream.api;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.stream.core.FanBizExecute;
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

    private final FanBizExecute fanBizExecute;


    @GetMapping("/raffle/join")
    public SingleResponse<Void> joinRaffle(@RequestParam @NotNull Integer toUid) {
        int uid = RequestContext.getLoginUID();
        ResponsePair<Void> res = fanBizExecute.joinRaffle(uid, toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
    @GetMapping("/raffle/get")
    public SingleResponse<RaffleCacheDTO> getRaffle(@RequestParam @NotNull Integer toUid) {
        ResponsePair<RaffleCacheDTO> res = fanBizExecute.getRaffleInfo(toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
    @GetMapping("/redpacket/get")
    public SingleResponse<RedPacketCacheDTO> getRedPacket(@RequestParam @NotNull Integer toUid) {
        ResponsePair<RedPacketCacheDTO> res = fanBizExecute.getRedPacketInfo(toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
    @GetMapping("/redpacket/join")
    public SingleResponse<Void> joinRedPacket(@RequestParam @NotNull Integer toUid) {
        int uid = RequestContext.getLoginUID();
        ResponsePair<Void> res = fanBizExecute.redWars(uid, toUid);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
}
