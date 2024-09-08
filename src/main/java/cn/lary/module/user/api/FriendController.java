package cn.lary.module.user.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.BasePageQuery;
import cn.lary.core.dto.PageQuery;
import cn.lary.core.dto.PageResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.core.dto.req.MsgCMDReq;
import cn.lary.kit.JSONKit;
import cn.lary.kit.ResKit;
import cn.lary.kit.StringKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.*;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.common.server.ShortNoConfig;
import cn.lary.module.common.service.SmsService;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.user.dto.req.FriendApplyReq;
import cn.lary.module.user.dto.res.FriendCodeCheck;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.dto.res.UserBasicInfo;
import cn.lary.module.user.entity.Friend;
import cn.lary.module.user.entity.FriendApplyRecord;
import cn.lary.module.user.entity.UserRedDot;
import cn.lary.module.user.service.*;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.entity.Request.message.MessageHeader;
import cn.lary.pkg.wk.entity.Request.message.MessageSendReq;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/friend")
@RequiredArgsConstructor
public class FriendController {

    private final RegisterConfig registerConfig;
    private final AppConfigService appConfigService;
    private final UserService userService;
    private final FriendService friendService;
    private final EventService eventService;
    private final RedisCache redisCache;
    private final RedisBizConfig redisBizConfig;
    private final WKUserService wkUserService;
    private final WKMessageService wkMessageService;
    private final GroupMemberService groupMemberService;
    private final GroupService groupService;
    private final FriendApplyRecordService friendApplyRecordService;
    private final UserRedDotService userRedDotService;
    private final KVBuilder kvBuilder;
    private final WKChannelService wkChannelService;
    private final SeqService seqService;
    private final LaryContentService laryContentService;

    /**
     *  查询用户申请列表
     * @param req {@link BasePageQuery}
     * @return {@link FriendApplyRecord}
     */
    @PostMapping("/apply/list")
    public PageResponse<FriendApplyRecord> friendApplyList(@RequestBody @Valid PageQuery req) {
        String uid = ReqContext.getLoginUID();
        long page = req.getPageIndex();
        long pageSize = req.getPageSize();
        Page<FriendApplyRecord> records = friendApplyRecordService.queryRecords(uid, page, pageSize);
        if(records == null) {
            return ResKit.pageFail("no valid records");
        }
        return ResKit.pageOk(records);
    }

    /**
     * 同意用户申请
     * @param token token
     * @return ok
     */
    @GetMapping("/apply/ack")
    public SingleResponse ackApply(@RequestParam(value = "token") @NotBlank String token) {
        String uid = ReqContext.getLoginUID();
        String name = ReqContext.getLoginName();
        if (StringKit.isEmpty(uid) || StringKit.isEmpty(name)) {
            return ResKit.fail(ResultCode.LOGIN_FIRST);
        }
        String K = kvBuilder.buildFriendApplyKey(token, uid);
        String value = redisCache.get(K);
        if (StringKit.isEmpty(value)) {
            return ResKit.fail("token expire,please wait resend apply");
        }
        Map<String,String> map = JSONKit.toMap(value);
        String fromUid = map.get("from_uid");
        String vercode = map.get("vercode");
        String remark = map.get("remark");
        if (StringKit.isEmpty(fromUid) || StringKit.isEmpty(vercode)) {
            return ResKit.fail("apply content error");
        }

        UserBaseRes userBaseRes = userService.queryBase(fromUid);
        if (userBaseRes == null || userBaseRes.getDeleted()) {
            return ResKit.fail("apply user not exist");
        }
        if (StringKit.isEmpty(remark)) {
            remark = "i am " + userBaseRes.getName();
        }
        boolean uidToFromUid = friendService.isFriend(uid, fromUid);
        long version = seqService.build(Lary.SeqKey.friend);
        // used not  friend
        if (!uidToFromUid) {
            // check source
            if (!checkSource(vercode, uid)) {
                return ResKit.fail("source error");
            }
            // insert db,add friend first time

            Friend friend = new Friend().setUid(uid).setToUid(fromUid).setVersion(version).setIsAlone(false).setIsInitiator(false)
                    .setSourceVercode(vercode).setVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.friend);
            friendService.save(friend);
        }else {
            // TODO  :  here use redis
            //update is_deleted
            Friend record = new Friend().setIsAlone(false).setIsInitiator(false).setSourceVercode(vercode).setVersion(version);
            friendService.update(record,new LambdaUpdateWrapper<Friend>().eq(Friend::getUid,uid).eq(Friend::getToUid,fromUid));
        }
        boolean fromUidToUid = friendService.isFriend(uid, fromUid);

        version = seqService.build(Lary.SeqKey.friend);
        if (!fromUidToUid) {
            Friend friend = new Friend().setUid(fromUid).setToUid(uid).setVersion(version).setIsAlone(false).setIsInitiator(true)
                    .setSourceVercode(vercode).setVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.friend);
            friendService.save(friend);
        }else {
            //update is_deleted
            Friend record = new Friend().setIsAlone(false).setIsInitiator(false).setSourceVercode(vercode).setVersion(version);
            friendService.update(record,new LambdaUpdateWrapper<Friend>().eq(Friend::getUid,fromUid).eq(Friend::getToUid,uid));
        }
        // send friend ack event
        HashMap<String, String> args = new HashMap<>();
        args.put("uid", uid);
        args.put("to_uid", fromUid);
        EventData data = new EventData().setEvent(Lary.Event.friendApplyAck).setType(Lary.EventType.none).setData(JSONKit.toJSON(args));
        eventService.begin(data);
        // TODO  :  here event not close
        // update friend request record
        boolean exists = friendApplyRecordService.exists(uid, fromUid);
        // It's unlikely that it doesn't exist
        if (exists) {
            FriendApplyRecord record = new FriendApplyRecord().setStatus(Lary.ApplyStatus.ok);
            friendApplyRecordService.update(record,new LambdaUpdateWrapper<FriendApplyRecord>().eq(FriendApplyRecord::getUid,uid).eq(FriendApplyRecord::getToUid,fromUid));
        }
        // send ack msg to other
        HashMap<String, String> param = new HashMap<>();
        param.put("uid", uid);
        param.put("from_uid", fromUid);
        param.put("from_name", userBaseRes.getName());
        MsgCMDReq cmdReq = new MsgCMDReq().setCmd(Lary.CMD.friendApplyAck).setSubscribers(List.of(uid, fromUid)).setParam(param);
        // wk service
        wkMessageService.send(cmdReq.convertCMD());

        MessageSendReq uidToApplyUid = new MessageSendReq();
        MessageHeader header = new MessageHeader();
        String p = laryContentService.buildFriendApplyAckMessage("hello,we can chat each other");
        header.setRedDot(1);
        uidToApplyUid.setHeader(header);
        uidToApplyUid.setFromUID(uid).setPayload(p.getBytes(StandardCharsets.UTF_8)).setChanelID(fromUid).setChannelType(WK.ChannelType.person);
        wkMessageService.send(uidToApplyUid);
        // send other message
        // convert req params
        p = laryContentService.buildFriendApplyAckMessage(remark);
        uidToApplyUid.setFromUID(fromUid).setPayload(p.getBytes(StandardCharsets.UTF_8)).setChanelID(uid);
        wkMessageService.send(uidToApplyUid);
        // remove redis cache
        redisCache.del(K);
        return ResKit.ok();
    }

    /**
     * 拒绝申请
     * @param toUid 请求加好友uid
     * @return ok
     */
    @GetMapping("/apply/refuse")
    public SingleResponse refuseApply(@RequestParam(value = "to_uid") @NotBlank String toUid) {
        String uid = ReqContext.getLoginUID();
        FriendApplyRecord record = friendApplyRecordService.queryByUidAndToUid(toUid, uid);
        if (record == null) {
            return ResKit.fail("no valid record");
        }
        FriendApplyRecord updateRecord = new FriendApplyRecord().setUid(toUid).setToUid(uid).setId(record.getId())
                .setRemark(record.getRemark()).setToken(record.getToken());
        updateRecord.setStatus(Lary.ApplyStatus.refused);
        friendApplyRecordService.updateById(updateRecord);
        return ResKit.ok();
    }
    @GetMapping("/apply/del")
    public SingleResponse deleteApply(@RequestParam(value = "to_uid") @NotBlank String toUid) {
        String uid = ReqContext.getLoginUID();
        friendApplyRecordService.deleteRecordByUidAndToUid(uid,toUid);
        return ResKit.ok();
    }

    /**
     * 申请加好友
     * @param req {@link FriendApplyReq}
     * @return ok
     */
    @PostMapping("/apply")
    public SingleResponse friendApply(@RequestBody @Valid FriendApplyReq req) {
        String uid = ReqContext.getLoginUID();
        String name = ReqContext.getLoginName();
        String toUid = req.getToUID();
        if (StringKit.same(uid,toUid)) {
            return ResKit.fail("can not apply friend to yourself");
        }
        UserBaseRes userBaseRes = userService.queryBase(uid);
        if (userBaseRes == null) {
            return ResKit.fail("user not exists");
        }
        UserBasicInfo toUser = userService.queryUserBasicInfo (toUid);
        if (toUser == null) {
            return ResKit.fail("applied user not exists");
        }

        // one way
        if (friendService.isFriend(toUid, uid) && friendService.isFriend(uid, toUid)) {
            return ResKit.fail("already friend");
        }
        // check friend source code
        String vercode = req.getVercode();
        if (StringKit.isEmpty(vercode)) {
            Friend friend = friendService.get(uid, toUid);
            if (friend == null) {
                return ResKit.fail("friend not exists");
            }
            if (StringKit.isEmpty(friend.getSourceVercode())){
                return ResKit.fail("source code is empty");
            }
            vercode = friend.getSourceVercode();
        }
        //check code is available
        boolean isOk = checkSource(vercode, uid);
        if (!isOk) {
            return ResKit.fail("source code error");
        }
        // set token
        String token = UUIDKit.getUUID();
        String K = kvBuilder.buildFriendApplyKey(token,toUid);
        String V = kvBuilder.buildFriendApplyValue(uid,vercode,req.getRemark());
        // set to redis
        redisCache.set(K,V,redisBizConfig.getFriendApplyExpire());
        // query apply record
        FriendApplyRecord record = friendApplyRecordService.queryByUidAndToUid(toUid, uid);
        UserRedDot userRedDot = userRedDotService.getUserRedDot(toUid, Lary.RedDot.friendApply);
        // TODO  :  here need transaction
        if (record == null) {
            FriendApplyRecord updateRecord = new FriendApplyRecord().setUid(toUid).setToUid(uid).setRemark(req.getRemark()).setToken(token).setStatus(Lary.ApplyStatus.notProcess);
            friendApplyRecordService.save(updateRecord);
        }else {
            // not first add
            if (record.getStatus() != Lary.ApplyStatus.notProcess) {
                FriendApplyRecord updateRecord = new FriendApplyRecord().setId(record.getId()).setUid(record.getUid()).setStatus(Lary.ApplyStatus.notProcess)
                .setRemark(req.getRemark()).setToUid(record.getToUid()).setToken(token);
                friendApplyRecordService.updateById(updateRecord);
            }
        }
        if (userRedDot == null) {
            UserRedDot updateRecord = new UserRedDot().setUid(toUid).setIsDot(true).setCategory(Lary.RedDot.friendApply).setCount(1);
            userRedDotService.save(updateRecord);
        }else {
            // not first add
            UserRedDot updateRecord = new UserRedDot().setId(userRedDot.getId()).setUid(toUid).setIsDot(true).setCategory(Lary.RedDot.friendApply).setCount(userRedDot.getCount() + 1);
            userRedDotService.updateById(updateRecord);
        }
        // send wk info
        MsgCMDReq cmdReq = new MsgCMDReq().setCmd(Lary.CMD.friendRequest).setChanelID(toUid).setChannelType(WK.ChannelType.person);
        HashMap<String, String> params = new HashMap<>();
        params.put("apply_uid", uid);
        params.put("apply_name", name);
        params.put("to_uid", toUid);
        params.put("token", token);
        params.put("remark", req.getRemark());
        cmdReq.setParam(params);
        wkMessageService.send(cmdReq.convertCMD());
        return ResKit.ok();
    }


    private boolean checkSource(String vercode,String uid) {

        String[] args = StringKit.split(vercode, "@");
        if(args == null || args.length < 2) {
            return false;
        }
        int codeType = getCodeType(args[1]);
        if (codeType == Lary.VerifyCode.unknown) {
            return false;
        }
        // check execute
        FriendCodeCheck check = switch (codeType) {
            // add friend by friend suggest
            case Lary.VerifyCode.friend -> friendService.checkByCode(vercode);
            // add friend by search
            case Lary.VerifyCode.user -> userService.checkByCode(vercode);
            // add friend by QR
            case Lary.VerifyCode.QR -> userService.checkByCode(vercode);
            // add friend by group
            case Lary.VerifyCode.groupMember -> groupMemberService.checkByCode(vercode);
            default -> null;
        };
        // check group setting
        if ( codeType == Lary.VerifyCode.groupMember) {
            // Check if the group exists
            GroupMember member = groupMemberService.getMemberByVerCode(vercode);
            if (member == null) {
                return false;
            }
            Group group = groupService.queryByNo(member.getGroupNo());
            if (group == null) {
                return false;
            }
            //group not allowed to add friend
            if (!group.getInvite() && member.getRole() == Lary.Group.Role.common) {
                return false;
            }
        }
        return check != null && !StringKit.diff(uid, check.getUid()) && !StringKit.diff(vercode, check.getVercode());
    }

    private int getCodeType(String code) {
        return switch (code) {
            case "1" -> Lary.VerifyCode.user;
            case "2" -> Lary.VerifyCode.groupMember;
            case "3" -> Lary.VerifyCode.QR;
            case "4" -> Lary.VerifyCode.friend;
            case "5" -> Lary.VerifyCode.mailList;
            case "6" -> Lary.VerifyCode.invitation;
            default -> Lary.VerifyCode.unknown;
        };
    }
}

