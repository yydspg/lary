package cn.lary.module.message.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.message.dto.req.ClearConversationUnreadReq;
import cn.lary.pkg.wk.api.WKConversationService;
import cn.lary.pkg.wk.dto.conversation.ConversationDeleteReq;
import cn.lary.pkg.wk.dto.conversation.ConversationUnreadClearReq;
import cn.lary.pkg.wk.vo.conversation.ConversationRes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/conversation/")
@RequiredArgsConstructor
public class ConversationController {

    private final WKConversationService wkConversationService;

    /**
     * 查询最近同步会话
     * @return {@link ConversationRes}
     */
    @GetMapping("/list")
    public MultiResponse list() {
        int uid = ReqContext.getLoginUID();
        Response<List<ConversationRes>> res = wkConversationService.list(uid);
        if (!res.isSuccessful()) {
            return ResponseKit.multiFail("server error");
        }
        return ResponseKit.multiOk(res.body());
    }

    /**
     * 清除某频道下的 最近会话未读数量
     * @param req {@link ClearConversationUnreadReq}
     * @return ok
     */
    @PostMapping("/clearUnread")
    public SingleResponse clearUnread(@Valid @RequestBody ClearConversationUnreadReq req) {
        int uid = ReqContext.getLoginUID();
        ConversationUnreadClearReq wkReq = new ConversationUnreadClearReq().setUid(uid).setMessageSeq(req.getMessageSeq());
//        wkReq.setChanelID(req.getChanelID()).setChannelType(req.getChannelType());
        Response<Void> res = wkConversationService.clearUnread(wkReq);
        if (!res.isSuccessful()) {
            log.error("wk clear unread error:{}",res.errorBody());
            return ResponseKit.fail("wk server error");
        }
        return ResponseKit.ok();
    }

    /**
     * 删除 某频道下的最近会话
     * @param channelId 频道id
     * @param channelType 频道类型
     * @return ok
     */
    @GetMapping("/del{channel_id}{channel_type}")
    public SingleResponse del(@NotNull @RequestParam(value = "channel_id") String channelId, @NotNull @RequestParam(value = "channel_type") byte channelType) {
        int uid = ReqContext.getLoginUID();
        ConversationDeleteReq wkReq = new ConversationDeleteReq();
//        wkReq.setChanelID(channelId).setChannelType(channelType);
        wkReq.setUid(uid);
        Response<Void> res = wkConversationService.delete(wkReq);
        if (!res.isSuccessful()) {
            log.error("wk del conversation error:{}",res.errorBody());
            return ResponseKit.fail("wk server error");
        }
        return ResponseKit.ok();
    }

}
