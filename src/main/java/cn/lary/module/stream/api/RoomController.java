package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.IPKit;
import cn.lary.kit.ResKit;
import cn.lary.kit.StringKit;
import cn.lary.module.stream.core.RoomBizExecute;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.dto.req.StartStreamReq;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
import cn.lary.pkg.wk.entity.core.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */

@Slf4j
@RestController
@RequestMapping("/v1/stream/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomBizExecute roomBizExecute;

    /**
     * 开启直播
     * @param req {@link StartStreamReq} 直播请求参数
     * @return {@link GoLiveVO}
     */
    @PostMapping("/start")
    public SingleResponse<GoLiveVO> start(@RequestBody @Valid GoLiveDTO req,HttpServletRequest request) {

        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        String ip = IPKit.getIp(request);
        ResPair<GoLiveVO> res = roomBizExecute.go(uid, uidName,ip, req);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

    /**
     * 加入直播间
     * @param toUid 主播id
     * @return {@link JoinLiveVO}
     */
    @GetMapping("/join{toUid}")
    public SingleResponse<JoinLiveVO> join(@PathVariable @NotNull String toUid, HttpServletRequest req) {
        String uid  = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        String ip = IPKit.getIp(req);
        ResPair<JoinLiveVO> res = roomBizExecute.join(uid, uidName, toUid,ip);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

    /**
     * 结束直播
     * @return ok
     */
    @GetMapping("/end")
    public SingleResponse<DownLiveVO> end() {
        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        ResPair<DownLiveVO> res = roomBizExecute.end(uid, uidName);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }

    /**
     * 离开直播间
     * @return ok
     */
    @GetMapping("/leave")
    public SingleResponse<Void> leave() {
        String uid = ReqContext.getLoginUID();
        ResPair<Void> res = roomBizExecute.leave(uid);
        if (!res.isOk()) {
            return ResKit.fail(res.getMsg());
        }
        return ResKit.ok(res.getData());
    }
}
