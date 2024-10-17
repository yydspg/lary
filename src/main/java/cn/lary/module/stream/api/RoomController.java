package cn.lary.module.stream.api;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.IPKit;
import cn.lary.kit.ResponseKit;
import cn.lary.module.stream.core.RoomBizExecute;
import cn.lary.module.stream.core.StreamBizExecute;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.dto.RaffleDTO;
import cn.lary.module.stream.dto.RedPacketDTO;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
import cn.lary.module.stream.vo.StreamRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  直播间
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */

@Slf4j
@RestController
@RequestMapping("/v1/room/")
@RequiredArgsConstructor
public class RoomController {

    private final RoomBizExecute roomBizExecute;
    private final StreamBizExecute streamBizExecute;

    /**
     * 直播记录查询
     * @param page p
     * @param limit s
     * @return {@link StreamRecordVO}
     */
    @GetMapping("/page")
    public PageResponse<StreamRecordVO> records(@RequestParam @NotNull Integer page, @RequestParam @NotNull Integer limit) {
        ResponsePair<List<StreamRecordVO>> res = streamBizExecute.page( page, limit);
        if (res.isFail()) {
            return ResponseKit.pageFail(res.getMsg());
        }
        return ResponseKit.pageOk(res.getData(),page,limit);
    }

    /**
     * 开启直播
     * @param req {@link GoLiveDTO} 直播请求参数
     * @return {@link GoLiveVO}
     */
    @PostMapping("/go")
    public SingleResponse<GoLiveVO> start(@RequestBody @Valid GoLiveDTO dto,HttpServletRequest request) {

        String ip = IPKit.getIp(request);
        ResponsePair<GoLiveVO> res = roomBizExecute.go(ip, dto);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 加入直播间
     * @param toUid 主播id
     * @return {@link JoinLiveVO}
     */
    @GetMapping("/join")
    public SingleResponse<JoinLiveVO> join(@RequestParam(value = "toUid") @NotNull Integer toUid, HttpServletRequest req) {
        String ip = IPKit.getIp(req);
        ResponsePair<JoinLiveVO> res = roomBizExecute.join( toUid,ip);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 结束直播
     * @return ok
     */
    @GetMapping("/end")
    public SingleResponse<DownLiveVO> end() {
        ResponsePair<DownLiveVO> res = roomBizExecute.end();
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 离开直播间
     * @return ok
     */
    @GetMapping("/leave")
    public SingleResponse<Void> leave() {
        ResponsePair<Void> res = roomBizExecute.leave();
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 创建抽奖事件
     * @param dto {@link RaffleDTO}
     * @return ok
     */
    @PostMapping("/raffle")
    public SingleResponse<Void> raffle(@RequestBody @Valid RaffleDTO dto) {
        ResponsePair<Void> res = roomBizExecute.raffle(dto);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 创建红包事件
     * @param dto {@link RedPacketDTO}
     * @return ok
     */
    @PostMapping("/redpacket")
    public SingleResponse<Void> redpacket(@RequestBody @Valid RedPacketDTO dto) {
        ResponsePair<Void> res = roomBizExecute.redPacket(dto);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
}
