package cn.lary.module.stream.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.ResKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.stream.dto.req.FollowListReq;
import cn.lary.module.stream.entity.FollowApply;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.service.FollowApplyService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.entity.UserShowInfo;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.entity.Request.message.MessageHeader;
import cn.lary.pkg.wk.entity.Request.message.MessageSendReq;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/stream/fan")
@RequiredArgsConstructor
public class FollowController {

    private final RoomService roomService;
    private final FollowApplyService followApplyService;
    private final UserService userService;
    private final WKMessageService wkMessageService;
    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;

    @GetMapping("/follow{toUid}{source}")
    public SingleResponse apply(@PathVariable @NotNull String toUid, @PathVariable @NotNull int source) {
        String uid = ReqContext.getLoginUID();
        String uidName = ReqContext.getLoginName();
        // check toUid whether exists
        UserBaseRes userBaseRes = userService.queryBase(uid);
        if (userBaseRes == null ) {
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
            Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.buildGoLiveKey(toUid));
            if (liveInfo == null) {
                // not in live
                MessageSendReq req = new MessageSendReq();
                req.setChanelID(toUid).setChannelType(WK.ChannelType.person);
                MessageHeader header = new MessageHeader().setRedDot(1).setNoPersist(1);
                String followMessage = uidName +  "关注了你";
                req.setHeader(header).setPayload(followMessage.getBytes(StandardCharsets.UTF_8));
                wkMessageService.send(req);
            }else {
                // to Uid living now
                String streamId = liveInfo.get("streamId").toString();
                Map<Object, Object> streamRecord = redisCache.getHash(kvBuilder.buildStreamRecordKey(toUid, streamId));
                if (streamRecord == null) {
                    return ResKit.fail("stream record not found");
                }
                redisCache.incrHash(kvBuilder.buildStreamRecordKey(toUid,streamId),"newFansNum");
            }
            // update room
            Room updateRecord = new Room().setUid(uid).setId(room.getId()).setFollowNum(room.getFollowNum() + 1).setUpdateBy(uid);
            roomService.updateById(updateRecord);
        }
        return ResKit.ok();
    }

    @GetMapping("/unfollow{toUid}")
    public SingleResponse unfollow(@PathVariable @NotNull String toUid) {
        String uid = ReqContext.getLoginUID();
        // check whether toUid exists
        UserBaseRes userBaseRes = userService.queryBase(uid);
        if (userBaseRes == null ) {
            return ResKit.fail("user not exists");
        }
        // check whether if this one is an anchor
        if (!roomService.isAnchor(toUid)) {
            return ResKit.fail(toUid+"is not anchor");
        }
        if (!followApplyService.isFan(uid,toUid)) {
            return ResKit.fail("you are not fan");
        }
        //check whether if toUid is living
        Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.buildGoLiveKey(toUid));
        if (liveInfo != null) {
            // living now
            String streamId = liveInfo.get("streamId").toString();
            //decr newFansNum
            redisCache.decrHash(kvBuilder.buildStreamRecordKey(toUid,streamId),"newFansNum");
        }
        followApplyService.update(new LambdaUpdateWrapper<FollowApply>().eq(FollowApply::getUid, uid).eq(FollowApply::getToUid, toUid)
                .set(FollowApply::getIsDelete,true).set(FollowApply::getUpdateBy,uid));
        return ResKit.ok();
    }

    @PostMapping("/page")
    public PageResponse<UserShowInfo> page(@RequestBody @Valid FollowListReq req) {
        String uid = ReqContext.getLoginUID();
        Page<FollowApply> page = new Page<>(req.getPageIndex(), req.getPageSize());

        Page<FollowApply> res = followApplyService.page(page, new LambdaQueryWrapper<FollowApply>().select(FollowApply::getToUid)
                .eq(FollowApply::getUid, uid).eq(FollowApply::getIsBlock, false).eq(FollowApply::getIsDelete, false)
                .orderByDesc(FollowApply::getUpdateAt));
        if (res == null) {
            return ResKit.pageFail("no valid data");
        }
        List<String> uids = new ArrayList<>();
        res.getRecords().stream().map(FollowApply::getUid).forEach(uids::add);
        // collect show info
        List<UserShowInfo> infos = userService.queryUserShowInfo(uids);
        return ResKit.pageOk(infos,res.getCurrent(),res.getSize(),res.getTotal());
    }
}
