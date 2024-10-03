package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.stream.core.FanBizExecute;
import cn.lary.module.stream.core.StreamBizExecute;
import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.dto.RedPacketCacheDTO;
import cn.lary.module.stream.vo.StreamRecordVO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    private final StreamBizExecute streamBizExecute;
    private final FanBizExecute fanBizExecute;


    @GetMapping("/raffle/join")
    public SingleResponse<Void> joinRaffle(@RequestParam @NotNull Integer toUid) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = fanBizExecute.joinRaffle(uid, toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
    @GetMapping("/raffle/get")
    public SingleResponse<RaffleCacheDTO> getRaffle(@RequestParam @NotNull Integer toUid) {
        ResPair<RaffleCacheDTO> res = fanBizExecute.getRaffleInfo(toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }
    @GetMapping("/redpacket/get")
    public SingleResponse<RedPacketCacheDTO> getRedPacket(@RequestParam @NotNull Integer toUid) {
        ResPair<RedPacketCacheDTO> res = fanBizExecute.getRedPacketInfo(toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }
    @GetMapping("/redpacket/join")
    public SingleResponse<Void> joinRedPacket(@RequestParam @NotNull Integer toUid) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = fanBizExecute.redWars(uid, toUid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok();
    }
}
