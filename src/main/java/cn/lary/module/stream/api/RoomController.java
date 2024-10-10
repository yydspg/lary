package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.ResPair;
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
        int uid = ReqContext.getLoginUID();
        ResPair<List<StreamRecordVO>> res = streamBizExecute.page(uid, page, limit);
        if (!res.isOk()) {
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
    public SingleResponse<GoLiveVO> start(@RequestBody @Valid GoLiveDTO req,HttpServletRequest request) {

        int uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        String ip = IPKit.getIp(request);
        ResPair<GoLiveVO> res = roomBizExecute.go(uid, uidName,ip, req);
        if (!res.isOk()) {
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
        int uid  = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        String ip = IPKit.getIp(req);
        ResPair<JoinLiveVO> res = roomBizExecute.join(uid, uidName, toUid,ip);
        if (!res.isOk()) {
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
        int uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        ResPair<DownLiveVO> res = roomBizExecute.end(uid, uidName);
        if (!res.isOk()) {
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
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = roomBizExecute.leave(uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 创建抽奖事件
     * @param req {@link RaffleDTO}
     * @return ok
     */
    @PostMapping("/raffle")
    public SingleResponse<Void> raffle(@RequestBody @Valid RaffleDTO req) {
        int uid = ReqContext.getLoginUID();
        ResPair<Void> res = streamBizExecute.raffle(uid, req);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 创建红包事件
     * @param req {@link RedPacketDTO}
     * @return ok
     */
    @PostMapping("/redpacket")
    public SingleResponse<Void> redpacket(@RequestBody @Valid RedPacketDTO req) {
        int uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        ResPair<Void> res = streamBizExecute.redPacket(uid, uidName, req);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
}
