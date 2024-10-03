package cn.lary.module.group.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.StringKit;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.SeqService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.AccountConfig;
import cn.lary.module.common.server.GroupConfig;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.dto.MemberAddDTO;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.group.service.GroupSettingService;
import cn.lary.module.group.vo.CreateGroupVO;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBizExecute {

    private final GroupService groupService;
    private final GroupConfig groupConfig;
    private final AppConfigService appConfigService;
    private final AccountConfig accountConfig;
    private final UserService userService;
    private final GroupMemberService groupMemberService;
    private final EventService eventService;
    private final GroupSettingService groupSettingService;
    private final KVBuilder kvBuilder;

    // external
    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    private final WKUserService wkUserService;

    /**
     * 创建群聊
     * @param creator 创建者 uid
     * @param req {@link CreateGroupDTO}
     * @return {@link CreateGroupVO}
     */
    public ResPair<CreateGroupVO> create(int creator, CreateGroupDTO req) {
        // check one day create limit
        int count = groupService.querySameDayCreateGroupCount(creator, LocalDateTime.now());
        if (groupConfig.getSameDayCreateMaxCount() <= count) {
            return BizKit.fail("reach max group create count");
        }
        // remove repeat
        List<Integer> members = CollectionKit.removeRepeat(req.getMembers());
        members.add(creator);

        List<User> users = userService.listByIds(members);
        List<User> validUser = users.stream()
                .filter(u -> u.getStatus() == Lary.UserStatus.ok && !u.getDeleted())
                .toList();
        // build group name
        String groupName = req.getName();
        if (StringKit.isEmpty(groupName)) {
            StringBuilder sb = new StringBuilder();
            validUser.forEach(u -> {
                sb.append(u.getUid()).append(",");
            });
            groupName = sb.toString();
        }
        Group group = new Group().setCreator(creator).setName(groupName);
        groupService.save(group);
        List<GroupMember> groupMembers = new ArrayList<>();
        validUser.forEach(u->{
            int role = Lary.Group.Role.common;
            if(u.getUid().equals(creator)) {
                role = Lary.Group.Role.creator;
            }
            GroupMember groupMember = new GroupMember()
                    .setUid(u.getUid())
                    .setRole(role)
                    .setGroupNo(group.getGroupNo());
            groupMembers.add(groupMember);
        });
        groupMemberService.saveBatch(groupMembers);

        CreateGroupVO vo = new CreateGroupVO(group.getGroupNo(), groupName, LocalDateTime.now());
        return BizKit.ok(vo);
    }

    /**
     * 管理员邀人加群
     * @param uid user id
     * @param req {@link MemberAddDTO}
     * @return ok
     */
    public ResPair<Void> addByAdmin(int uid, MemberAddDTO req) {
        GroupMember inviter = groupMemberService.getOne(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupNo, req.getGroupId())
                .eq(GroupMember::getUid,uid));
        if (inviter == null) {
            return BizKit.fail("inviter not exist,uid:" + uid);
        }
        if (inviter.getRole() == Lary.Group.Role.common) {
            return BizKit.fail("你无权限操作" + uid);
        }
        Group group = groupService.getOne(new LambdaQueryWrapper<Group>()
                .eq(Group::getGroupNo, req.getGroupId()));
        if (group == null) {
            return BizKit.fail("group not exist,groupId:" + req.getGroupId());
        }
        List<Integer> members = CollectionKit.removeRepeat(req.getMembers());
        List<User> users = userService.listByIds(members);
        List<User> validUser = users.stream().filter(u -> u.getStatus() == Lary.UserStatus.ok && !u.getDeleted()).toList();
        List<Integer> blockUsers = groupMemberService.queryMemberWithStatus(req.getGroupId(), Lary.Group.UserStatus.block);
        List<Integer> existsUsers = groupMemberService.queryMemberWithStatus(req.getGroupId(), Lary.Group.UserStatus.common);
        List<Integer> addUsers = new ArrayList<>();
        validUser.forEach(u->{
            if (!blockUsers.contains(u.getUid()) && !existsUsers.contains(u.getUid())) {
                addUsers.add(u.getUid());
            }
        });
        if(CollectionKit.isEmpty(addUsers)) {
            return BizKit.fail("no valid users add" );
        }
        // insert
        List<GroupMember> groupMembers = new ArrayList<>();
        addUsers.forEach(u->{
            GroupMember groupMember = new GroupMember()
                    .setUid(u)
                    .setRole(Lary.Group.Role.common)
                    .setInviteUid(uid)
                    .setGroupNo(req.getGroupId());
            groupMembers.add(groupMember);
        });

        groupMemberService.saveBatch(groupMembers);

        return BizKit.ok();
    }

    /**
     * 主动加群
     * @param uid user id
     * @param groupId group id
     * @param uidName user name
     * @return ok
     */
    public ResPair<Void> add(int uid,int groupId,String uidName) {

        List<Integer> blockUsers = groupMemberService.queryMemberWithStatus(groupId, Lary.Group.UserStatus.block);
        if (blockUsers.contains(uid)) {
            return BizKit.fail("u was blocked");
        }
        GroupMember member = groupMemberService.getOne(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupNo, groupId)
                .eq(GroupMember::getUid, uid));
        if (member == null) {
            groupMemberService.save(new GroupMember().setGroupNo(groupId).setUid(uid));
        }else {
            groupMemberService.update(new LambdaUpdateWrapper<GroupMember>()
                    .set(GroupMember::getIsDeleted,false)
                    .set(GroupMember::getRole,Lary.Group.Role.common));
        }
        String content = uidName + "加入了群聊";
        MessageSendDTO sendDTO = new MessageSendDTO()
                .setChannelID(groupId)
                .setChannelType(WK.ChannelType.group)
                .setFromUID(uid)
                .setPayload(content.getBytes(StandardCharsets.UTF_8));
        wkMessageService.send(sendDTO);
        return BizKit.ok();
    }

    /**
     * 封禁用户
     * @param uid u
     * @param groupId g
     * @param toUid t
     * @return ok
     */
    public ResPair<Void> block(int uid,int groupId,int toUid) {
        GroupMember member = groupMemberService.getOne(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupNo, groupId)
                .eq(GroupMember::getUid, uid));
        if (member == null) {
            return BizKit.fail("you not member");
        }
        if (member.getRole() == Lary.Group.Role.common) {
            return BizKit.fail("no rights to block");
        }
        GroupMember toMember = groupMemberService.getOne(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupNo, groupId)
                .eq(GroupMember::getUid, toUid));
        if (toMember != null && toMember.getRole() == Lary.Group.Role.common) {
            groupMemberService.update(new LambdaUpdateWrapper<GroupMember>()
                    .set(GroupMember::getStatus,Lary.Group.UserStatus.block)
                    .eq(GroupMember::getUid, uid)
                    .eq(GroupMember::getGroupNo,groupId));
        }
        return BizKit.ok();
    }
    /**
     * 退出群聊
     * @param uid user id
     * @param groupId groupId
     * @return ok
     */
    public ResPair<Void> quit(int uid,int groupId) {

        GroupMember member = groupMemberService.getOne(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupNo, groupId)
                .eq(GroupMember::getUid, uid));
        if (member == null) {
            return BizKit.fail("you not member");
        }
        if (member.getRole() == Lary.Group.Role.creator) {
            return BizKit.fail("creator can not quit");
        }
        groupMemberService.update(new LambdaUpdateWrapper<GroupMember>()
                .set(GroupMember::getIsDeleted,true)
                .eq(GroupMember::getGroupNo, groupId)
                .eq(GroupMember::getUid,uid));
        return BizKit.ok();
    }



    //        if(existsUsers.size() < 9 && !group.getIsUploadAvatar() ){
//            if(addUsers.size() < 9 - existsUsers.size()) {
//                addUsers.addAll(existsUsers);
//            }else {
//                for (Long u : existsUsers) {
//                    addUsers.add(u);
//                    if (addUsers.size() == 9) {
//                        break;
//                    }
//                }
//            }
//        }
}
