package cn.lary.module.message.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.message.dto.req.ClearConversationUnreadReq;
import cn.lary.pkg.wk.api.WKConversationService;
import cn.lary.pkg.wk.entity.Request.conversation.ConversationDeleteReq;
import cn.lary.pkg.wk.entity.Request.conversation.ConversationUnreadClearReq;
import cn.lary.pkg.wk.entity.Response.conversation.ConversationRes;
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
        String uid = ReqContext.getLoginUID();
        Response<List<ConversationRes>> res = wkConversationService.list(uid);
        if (!res.isSuccessful()) {
            return ResKit.multiFail("server error");
        }
        return ResKit.multiOk(res.body());
    }

    /**
     * 清除某频道下的 最近会话未读数量
     * @param req {@link ClearConversationUnreadReq}
     * @return ok
     */
    @PostMapping("/clearUnread")
    public SingleResponse clearUnread(@Valid @RequestBody ClearConversationUnreadReq req) {
        String uid = ReqContext.getLoginUID();
        ConversationUnreadClearReq wkReq = new ConversationUnreadClearReq().setUid(uid).setMessageSeq(req.getMessageSeq());
        wkReq.setChanelID(req.getChanelID()).setChannelType(req.getChannelType());
        Response<Void> res = wkConversationService.clearUnread(wkReq);
        if (!res.isSuccessful()) {
            log.error("wk clear unread error:{}",res.errorBody());
            return ResKit.fail("wk server error");
        }
        return ResKit.ok();
    }

    /**
     * 删除 某频道下的最近会话
     * @param channelId 频道id
     * @param channelType 频道类型
     * @return ok
     */
    @GetMapping("/del{channel_id}{channel_type}")
    public SingleResponse del(@NotNull @PathVariable(value = "channel_id") String channelId, @NotNull @PathVariable(value = "channel_type") byte channelType) {
        String uid = ReqContext.getLoginUID();
        ConversationDeleteReq wkReq = new ConversationDeleteReq();
        wkReq.setChanelID(channelId).setChannelType(channelType);
        wkReq.setUid(uid);
        Response<Void> res = wkConversationService.delete(wkReq);
        if (!res.isSuccessful()) {
            log.error("wk del conversation error:{}",res.errorBody());
            return ResKit.fail("wk server error");
        }
        return ResKit.ok();
    }

}
