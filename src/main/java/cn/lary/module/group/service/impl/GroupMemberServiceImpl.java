package cn.lary.module.group.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.mapper.GroupMemberMapper;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.vo.GroupMemberVO;
import cn.lary.module.group.vo.GroupVO;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {

    private final UserService userService;

    @Override
    public ResPair<List<Integer>> getMembersWithStatus(int groupNo, int status) {
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId, groupNo)
                .eq(GroupMember::getStatus, status)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return BizKit.ok(Collections.emptyList());
        }
        List<Integer> vos = new ArrayList<>(data.size());
        data.forEach(t-> vos.add(t.getUid()));
        return BizKit.ok(vos);
    }

    @Override
    public ResPair<Void> quit(int groupId) {
        int uid = RequestContext.getLoginUID();
        GroupMember member = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid)
                .one();
        if (member == null) {
            return BizKit.fail("not group member");
        }
        if (member.getRole() == Lary.Group.Role.creator) {
            return BizKit.fail("creator can not quit");
        }
        lambdaUpdate()
                .set(GroupMember::getIsDelete,true)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid,uid);
        return BizKit.ok();
    }

    @Override
    public ResPair<Void> quitByAdmin(int groupId, int uid) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getIsDelete,true)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid);
        return BizKit.ok();
    }

    @Override
    public ResPair<Void> join(int groupId) {
        return join(RequestContext.getLoginUID(),groupId,0);
    }

    @Override
    public ResPair<Void> joinByAdmin(int groupId, int uid) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        return join(uid,groupId,res.getData().getUid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResPair<Integer> multiJoinByAdmin(int groupId, List<Integer> ids) {
        if (CollectionKit.isEmpty(ids)){
            return BizKit.fail("user empty");
        }
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        List<Integer> members = CollectionKit.removeRepeat(ids);
        List<Integer> users = userService.getValidUsers(members);
        if (CollectionKit.isEmpty(users)) {
            return BizKit.fail("valid users empty");
        }
        ResPair<List<Integer>> blockUsers = this.getMembersWithStatus(groupId, Lary.Group.UserStatus.block);
        ResPair<List<Integer>> existsUsers = this.getMembersWithStatus(groupId, Lary.Group.UserStatus.common);

        if(!blockUsers.isOk() || CollectionKit.isEmpty(blockUsers.getData())){
            return BizKit.fail(res.getMsg());
        }
        if(!existsUsers.isOk() || CollectionKit.isEmpty(existsUsers.getData())){
            return BizKit.fail(res.getMsg());
        }
        List<Integer> addUsers = new ArrayList<>();
        users.forEach(u->{
            if (!blockUsers.getData().contains(u) && !existsUsers.getData().contains(u)){
                addUsers.add(u);
            }
        });
        if (CollectionKit.isEmpty(addUsers)){
            return BizKit.fail("no valid users");
        }
        List<GroupMember> groupMembers = new ArrayList<>();
        addUsers.forEach(u -> {
            GroupMember groupMember = new GroupMember()
                    .setUid(u)
                    .setRole(Lary.Group.Role.common)
                    .setInviteUid(RequestContext.getLoginUID())
                    .setGroupId(groupId);
            groupMembers.add(groupMember);
        });
        saveBatch(groupMembers);
        return BizKit.ok();
    }

    @Override
    public ResPair<Void> setAdmin(int groupId, int uid) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getRole,Lary.Group.Role.manager)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid);
        return BizKit.ok();
    }

    @Override
    public ResPair<Void> removeAdmin(int groupId, int uid) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getRole,Lary.Group.Role.common)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid);
        return null;
    }

    @Override
    public ResPair<List<GroupMemberVO>> members(int groupId) {
        GroupMember operator = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, RequestContext.getLoginUID())
                .one();
        if (operator == null || operator.getIsDelete()
                || operator.getStatus() == Lary.Group.UserStatus.block) {
            return BizKit.fail("status error");
        }
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getUid)
                .select(GroupMember::getRole)
                .select(GroupMember::getStatus)
                .select(GroupMember::getNickname)
                .select(GroupMember::getName)
                .select(GroupMember::getAvatar)
                .eq(GroupMember::getGroupId, groupId)
                .list();
        if (CollectionKit.isEmpty(data)){
            return BizKit.fail("data empty");
        }
        List<GroupMemberVO> vos = new ArrayList<>(data.size());
        data.forEach(t->{
            GroupMemberVO vo = new GroupMemberVO();
            vos.add(vo.of(t));
        });
        return BizKit.ok(vos);
    }

    @Override
    public ResPair<Void> changeOwner(int groupId, int uid) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getRole, Lary.Group.Role.creator)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid);
        lambdaUpdate()
                .set(GroupMember::getRole,Lary.Group.Role.manager)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid, res.getData().getUid());
        return BizKit.ok();
    }

    @Override
    public ResPair<Void> disband(int groupId) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getIsDelete,true)
                .eq(GroupMember::getGroupId,groupId);
        return BizKit.ok();
    }

    private ResPair<Void> join(int uid,int groupId,int inviteId){
        GroupMember operator = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
        if (operator != null && operator.getStatus() == Lary.Group.UserStatus.block) {
            return BizKit.fail("been blocked");
        }
        if (operator == null) {
            this.save(new GroupMember()
                    .setGroupId(groupId)
                    .setUid(uid)
                    .setRole(Lary.Group.Role.common)
                    .setVerifyCode(UUIDKit.getUUID()));
        }else {
            lambdaUpdate()
                    .set(GroupMember::getIsDelete,false)
                    .eq(GroupMember::getGroupId, groupId)
                    .set(GroupMember::getInviteUid, inviteId)
                    .eq(GroupMember::getUid, uid);
        }
        return BizKit.ok();
    }

    public  ResPair<GroupMember> checkRole(int groupId) {
        int operator = RequestContext.getLoginUID();
        GroupMember admin = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, operator)
                .one();
        if (admin == null) {
            return BizKit.fail("not group member");
        }
        if (admin.getRole() == Lary.Group.Role.common) {
            return BizKit.fail("no privilege");
        }
        return BizKit.ok(admin);
    }

    @Override
    public ResPair<List<Integer>> my(int role) {
        int uid = RequestContext.getLoginUID();
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getGroupId)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, role)
                .list();
        if (CollectionKit.isEmpty(data)){
            return BizKit.fail("data empty");
        }
        List<Integer> vos = new ArrayList<>(data.size());
        data.forEach(t->{
            vos.add(t.getGroupId());
        });
        return BizKit.ok(vos);
    }

    @Override
    public ResPair<Void> block(int groupId, int uid) {
        ResPair<GroupMember> res = checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getStatus,Lary.Group.UserStatus.block)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid);
        return BizKit.ok();
    }
}
