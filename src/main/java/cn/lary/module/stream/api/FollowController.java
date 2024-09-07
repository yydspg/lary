package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.stream.entity.FollowApply;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.service.FollowApplyService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.entity.Request.message.MessageHeader;
import cn.lary.pkg.wk.entity.Request.message.MessageSendReq;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/v1/stream/follow")
@RequiredArgsConstructor
public class FollowController {
    private final RoomService roomService;
    private final FollowApplyService followApplyService;
    private final UserService userService;
    private final WKMessageService wkMessageService;

    @RequestMapping("/apply")
    public SingleResponse apply(@PathVariable @NotNull String toUid, @PathVariable @NotNull int source) {
        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        // check toUid whether exists
        UserBaseRes userBaseRes = userService.queryBase(uid);
        if (userBaseRes == null || userBaseRes.getDeleted()) {
            return ResKit.fail(ResultCode.USER_NO_EXIST);
        }
        Room room = roomService.getOne(new LambdaQueryWrapper<Room>().eq(Room::getUid, uid));
        if (room == null || room.getIsDelete()) {
            return ResKit.fail("user has no room");
        }
        if (room.getIsBlock()) {
            return ResKit.fail("user has blocked room,due to"+room.getBlockDescription());
        }
        // set source
        FollowApply one = followApplyService.getOne(new LambdaQueryWrapper<FollowApply>().eq(FollowApply::getUid, uid).eq(FollowApply::getToUid, toUid));
        if (one != null && one.getIsBlock()) {
            return ResKit.fail("you been blocked");
        }else {
            FollowApply followApply = new FollowApply().setSource(source).setUid(uid).setToUid(toUid).setCreateBy(uid);
            followApplyService.save(followApply);
            // send message info
            MessageSendReq req = new MessageSendReq();
            req.setChanelID(toUid).setChannelType(WK.ChannelType.person);
            MessageHeader header = new MessageHeader().setRedDot(1).setNoPersist(1);
            String followMessage = uidName +  "关注了你";
            req.setHeader(header).setPayload(followMessage.getBytes(StandardCharsets.UTF_8));
            wkMessageService.send(req);
            // update room
            room.setFollowNum(room.getFollowNum() + 1);
            roomService.saveOrUpdate(room);
        }
        return ResKit.ok();
    }
}
