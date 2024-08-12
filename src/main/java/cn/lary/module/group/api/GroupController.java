package cn.lary.module.group.api;

import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.*;
import cn.lary.module.app.entity.AppConfigRes;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.server.AccountConfig;
import cn.lary.module.common.server.GroupConfig;
import cn.lary.module.group.dto.req.*;
import cn.lary.module.group.dto.res.GroupDetailRes;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupDetail;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.group.service.GroupSettingService;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserBase;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.entity.Request.channel.ChannelCreateReq;
import cn.lary.pkg.wk.entity.Request.channel.SubscribersAddReq;
import cn.lary.pkg.wk.entity.core.WK;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/group/")
@RequiredArgsConstructor
public class GroupController {


    private final GroupService groupService;
    private final GroupConfig groupConfig;
    private final AppConfigService appConfigService;
    private final AccountConfig accountConfig;
    private final UserService userService;
    private final GroupMemberService groupMemberService;
    private final EventService eventService;
    private final GroupSettingService groupSettingService;
    private final WKChannelService wkChannelService;

    @PostMapping("/create")
    public SingleResponse createGroup(@Valid @RequestBody GroupCreate req) {

        int count = groupService.querySameDayCreateGroupCount(req.getUid(), LocalDateTime.now());
        if (groupConfig.getSameDayCreateMaxCount() <= count) {
            return ResKit.fail("reach max group create count");
        }
        String creator = req.getUid();
        String createName = req.getCreateName();
        // remove repeat
        List<String> uids = CollectionKit.removeRepeat(req.getMembers());
        // check friendship

        // check system count
        AppConfigRes appConfig = appConfigService.getAppConfig();
        if (appConfig != null && appConfig.isInviteSystemAccountJoinGroupOn() ) {
            boolean containsSystemAccount = false;
            for (String uid : uids) {
                if(StringKit.same(uid,accountConfig.getFileHelperUid())) {
                    containsSystemAccount = true;
                    break;
                }
            }
            if (containsSystemAccount) {
                return ResKit.fail(ResultCode.APP_CONFIG_NO_EXIST);
            }
        }
        User user = userService.queryByUID(req.getUid());
        if (user == null) {
            return ResKit.fail("user not exist");
        }
        // add create user
        uids.add(req.getUid());
        List<UserBaseRes> users = userService.queryUserBaseInfoByUIDs(uids);
        //check user
        if (CollectionKit.isEmpty(users)) {
            return ResKit.fail("no valid  user id");
        }
        // remove user
        List<UserBaseRes> realUsers = users.stream().filter(t -> !t.getDeleted()).toList();
        ArrayList<UserBase> realUserList = new ArrayList<>();
        realUsers.forEach(u -> {
            UserBase userBase = new UserBase().setName(u.getName()).setUid(u.getUid());
            realUserList.add(userBase);
        });
        ArrayList<String> realUids = new ArrayList<>();
        realUsers.forEach(u -> {realUids.add(u.getUid());});
        if (CollectionKit.isEmpty(realUsers)) {
            return ResKit.fail("no real user");
        }
        // build destroy user
        List<UserBaseRes> deletedUsers = users.stream().filter(UserBaseRes::getDeleted).toList();
        List<UserBase> deletedUserList = new ArrayList<UserBase>();
        deletedUsers.forEach(t->{
            UserBase userBase = new UserBase().setUid(t.getUid()).setName(t.getName());
            deletedUserList.add(userBase);
        });
        // build group name
        String groupName = req.getName();

        if (StringKit.isEmpty(groupName)) {
            // build by group member name
            StringBuilder sb = new StringBuilder();
            for (String uid : realUids) {
                sb.append(uid).append(",");
            }
            groupName = sb.toString();
        }
        // create group no
        String groupNo = UUIDKit.getUUID();
        // group seq
        long version = 0;
        // TODO  :  tx
        Group group = new Group().setGroupNo(groupNo).setCreator(user.getUid()).setName(groupName).setAllowViewHistoryMsg(groupConfig.isAllowViewHistoryMsgStatus());
        groupService.save(group);

        // add group member,filter
        realUsers.forEach(u -> {
            short memberRole = Lary.Group.Role.common;
            if (StringKit.same(u.getUid(),creator)) {
                memberRole = Lary.Group.Role.creator;
            }
            GroupMember groupMember = new GroupMember().setUid(u.getUid()).setGroupNo(groupNo).setRole(memberRole).setRobot(u.getIsRobot());
            groupMemberService.save(groupMember);
        });
        // start group  member add event
        MsgGroupCreate usefulData = new MsgGroupCreate().setUsers(realUserList).setGroupNo(groupNo).setCreator(creator).setCreateName(createName).setVersion(version);
        EventData data = new EventData().setEvent(Lary.Event.groupCreate).setType(Lary.EventType.message).setData(JSONKit.toJSON(usefulData));
        eventService.begin(data);
        // start unable add group member event
        if(CollectionKit.isNotEmpty(deletedUserList)) {
            MsgGroupCreate deletedData = new MsgGroupCreate().setCreateName(createName).setVersion(version).setCreator(creator).setUsers(deletedUserList).setGroupNo(groupNo);
            EventData unableAddAccountData = new EventData().setEvent(Lary.Event.groupUnableAddDestroyAccount).setType(Lary.EventType.message).setData(JSONKit.toJSON(deletedData));
            eventService.begin(unableAddAccountData);
        }
        // start group avatar upload event
        CMDGroupAvatarUpdate cmdGroupAvatarUpdate = new CMDGroupAvatarUpdate().setRealUids(realUids).setGroupNo(groupNo);
        EventData groupAvatarUpdate = new EventData().setEvent(Lary.Event.groupAvatarUpdate).setType(Lary.EventType.cmd).setData(JSONKit.toJSON(cmdGroupAvatarUpdate));
        eventService.begin(groupAvatarUpdate);

        // create IM channel
        ChannelCreateReq channelCreateReq = new ChannelCreateReq().setSubscribers(realUids);
        channelCreateReq.setChanelID(groupNo);
        channelCreateReq.setChannelType(WK.ChannelType.group);
        wkChannelService.createOrUpdate(channelCreateReq);
        Group res = groupService.queryByNo(groupNo);
        return ResKit.ok(res);
    }
    @GetMapping("/list")
    public MultiResponse list(@NotNull(message = "uid is null") @RequestParam(value = "uid") String uid) {
        List<GroupDetail> groupDetails = groupService.querySavedGroups(uid);
        if (CollectionKit.isEmpty(groupDetails)) {
            return ResKit.multiFail("no valid data");
        }
        ArrayList<GroupDetailRes> list = new ArrayList<>();
        for (GroupDetail groupDetail : groupDetails) {
            list.add(GroupDetailRes.of(groupDetail));
        }
        return ResKit.multiOk(list);
    }
    @PostMapping("/{groupNo}/add")
    public SingleResponse addMember(@PathVariable(value = "groupNo") String groupNo, @Valid @RequestBody MemberAdd req) {
        // check Group if exists
        Group group = groupService.queryByNo(groupNo);
        if (group == null ) {
            return ResKit.fail(ResultCode.GROUP_NO_EXIST);
        }
        String creator = group.getCreator();
        String inviter = req.getUid();
        List<String> members = req.getMembers();
        // check system account
        AppConfigRes appConfig = appConfigService.getAppConfig();
        if (appConfig == null) {
            return ResKit.fail(ResultCode.APP_CONFIG_NO_EXIST);
        }
        // enable system account join group
        if (appConfig.isNewUserJoinSystemGroup()) {
            boolean containsSystemAccount = false;
            for (String uid : members) {
                if(StringKit.same(uid,accountConfig.getFileHelperUid())) {
                    containsSystemAccount = true;
                    break;
                }
            }
            if (containsSystemAccount) {
                return ResKit.fail(ResultCode.APP_CONFIG_NO_EXIST);
            }
        }
        // invite pattern
        if(group.isInvite()) {
            if (!groupMemberService.isCreatorOrManager(inviter,groupNo,creator)) {
                return ResKit.fail(ResultCode.NO_CREATOR_OR_MANAGER);
            }
        }
        // insert member

    }
    private SingleResponse addGroupMember(List<String> members,String creator,String groupNo) {
        // query if creator in group
        if (!groupMemberService.isMember(creator,groupNo)) {
            return ResKit.fail("creator is not member");
        }
        // get real uids
        List<String> uids = CollectionKit.removeRepeat(members);
        List<UserBaseRes> userBaseList = userService.queryUserBaseInfoByUIDs(uids);
        List<UserBaseRes> newUserList = new ArrayList<>();
        List<UserBaseRes> unableAddUserList = new ArrayList<>();
        userBaseList.forEach(user -> {
            UserBaseRes userBase = new UserBaseRes().setName(user.getName()).setUid(user.getUid()).setDeleted(user.getDeleted()).setIsRobot(user.getIsRobot());
            if (user.getDeleted()) {
                unableAddUserList.add(userBase);
            }else {
                newUserList.add(userBase);
            }
        });
        if (userBaseList.size() == unableAddUserList.size()) {
            return ResKit.fail("user all deleted");
        }
        // get block member list
        List<String> blockList = groupMemberService.queryMemberWithStatus(groupNo, Lary.Group.UserStatus.block);
        List<String> existsList = groupMemberService.queryMemberWithStatus(groupNo, Lary.Group.UserStatus.common);
        //build real add user
        List<UserBaseRes> realList = new ArrayList<>();
        List<String> realUIds = new ArrayList<>();
        for (UserBaseRes userBase : newUserList) {
            if (!blockList.contains(userBase.getUid()) && !existsList.contains(userBase.getUid())) {
                realList.add(userBase);
                realUIds.add(userBase.getUid());
            }
        }
        if (CollectionKit.isEmpty(realList)) {
            return ResKit.fail("no valid id");
        }
        // add members to group
        realList.forEach(user -> {
            // TODO  :  version gen seq
            boolean isDeletedMember = groupMemberService.isDeletedMember(groupNo, user.getUid());
            if (isDeletedMember) {
                groupMemberService.recoveryMember(groupNo, user.getUid());
            }else {
                GroupMember groupMember = new GroupMember().setUid(user.getUid()).setGroupNo(groupNo).setRobot(user.getIsRobot());
                groupMember.setInviteUid(creator).setVercode(BizKit.buildGroupMemberVerCode()).setVersion(null);
                groupMemberService.save(groupMember);
            }
        });
        // start member add event
        MsgGroupMemberAdd msgGroupMemberAdd = new MsgGroupMemberAdd().setOperator(creator).setMembers(realList).setGroupNo(groupNo);
        EventData memberAdd = new EventData().setEvent(Lary.Event.groupMemberAdd).setType(Lary.EventType.message).setData(JSONKit.toJSON(msgGroupMemberAdd));
        eventService.begin(memberAdd);
        // set avatar upload event
        /*
        if member count less than 9 and group creator not update avatar,need create
        group avatar event , if u do understand why,plz use qq to create a group and have a look
         */
        boolean uploadAvatar = groupService.isUploadAvatar(groupNo);
        long memberCount = groupMemberService.queryMemberCount(groupNo);
        // ensure condition
        int max = groupConfig.getMaxGroupMemberCountAboutAvatar();
        List<String> oldMembers = groupMemberService.queryMemberWithLimit(groupNo, max);
        List<String> updateRecord = new ArrayList<>(oldMembers);
        if (!uploadAvatar && memberCount <= max) {
            if (realList.size() + oldMembers.size() > max) {
                for (int i = 0; i < realList.size(); i++) {
                    if (updateRecord.size() == max) {
                        break;
                    }
                    updateRecord.add(realList.get(i).getUid());
                }
            }else {
                realList.forEach(user -> {
                    oldMembers.add(user.getUid());
                });
            }
        }
        //start group avatar event
        MsgGroupAvatarUpdate msgGroupAvatarUpdate = new MsgGroupAvatarUpdate().setGroupNo(groupNo).setMembers(updateRecord);
        EventData data = new EventData().setEvent(Lary.Event.groupAvatarUpdate).setType(Lary.EventType.cmd).setData(JSONKit.toJSON(msgGroupAvatarUpdate));
        eventService.begin(data);
        // wk im subscribers add
        SubscribersAddReq subscribersAddReq = new SubscribersAddReq();
        subscribersAddReq.setChanelID(groupNo).setChannelType(WK.ChannelType.group);
        subscribersAddReq.setSubscribers(realUIds);
        wkChannelService.addSubscribers(subscribersAddReq);
        return ResKit.ok();
    }
}

