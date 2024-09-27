package cn.lary.module.message.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.SingleResponse;
import cn.lary.core.dto.req.MsgCMDReq;
import cn.lary.kit.ResKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.entity.core.WK;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/v1/message/")
@RequiredArgsConstructor
public class MessageController {


    private final WKMessageService wkMessageService;

    /**
     * 发送输入中命令
     * @return ok
     */
    @GetMapping("/typing")
    public SingleResponse typing(@RequestParam(value = "channel_id") @NotNull String  channelId, @RequestParam(value = "channel_type") @NotNull byte channelType) {
        Integer uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();

        if (WK.ChannelType.person == channelType) {
            channelId = uid.toString();
        }
        HashMap<String, String> args = new HashMap<>();
        args.put("from_uid", uid.toString());
        args.put("from_name", uidName);
        args.put("channel_id",channelId);
        args.put("channel_type",String.valueOf(channelType));
        MsgCMDReq cmdReq = new MsgCMDReq().setChanelID(channelId).setChannelType(channelType).setCmd(Lary.CMD.typing).setPersist(false).setParam(args);
        wkMessageService.send(cmdReq.convertCMD());
        return ResKit.ok();
    }

}
