package cn.lary.module.group.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.UUIDKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.mapper.GroupMemberMapper;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.vo.GroupMemberVO;
import cn.lary.module.user.service.UserService;
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
    public ResponsePair<List<Long>> getMembersWithStatus(long groupNo, int status) {
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId, groupNo)
                .eq(GroupMember::getStatus, status)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return BusinessKit.ok(Collections.emptyList());
        }
        List<Long> vos = new ArrayList<>(data.size());
        data.forEach(t-> vos.add(t.getUid()));
        return BusinessKit.ok(vos);
    }

    @Override
    public ResponsePair<Void> quit(long groupId) {
        long uid = RequestContext.getLoginUID();
        GroupMember member = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid)
                .one();
        if (member == null) {
            return BusinessKit.fail("not group member");
        }
        if (member.getRole() == LARY.GROUP.ROLE.CREATOR) {
            return BusinessKit.fail("creator can not quit");
        }
        lambdaUpdate()
                .set(GroupMember::getIsDelete,true)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid,uid);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> quitByAdmin(long groupId, long uid) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getIsDelete,true)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> join(long groupId) {
        return join(RequestContext.getLoginUID(),groupId,0);
    }

    @Override
    public ResponsePair<Void> joinByAdmin(long groupId, long uid) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        return join(uid,groupId,response.getData().getUid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponsePair<List<Long>> multiJoinByAdmin(long groupId, List<Long> ids) {
        if (CollectionKit.isEmpty(ids)){
            return BusinessKit.fail("user empty");
        }
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        List<Long> members = CollectionKit.removeRepeat(ids);
        List<Long> users = userService.getValidUsers(members);
        if (CollectionKit.isEmpty(users)) {
            return BusinessKit.fail("valid users empty");
        }
        ResponsePair<List<Long>> blockPair = this.getMembersWithStatus(groupId, LARY.STATUS.BLOCK);
        ResponsePair<List<Long>> exsitsPair = this.getMembersWithStatus(groupId, LARY.STATUS.COMMON);

        if(blockPair.isFail() || CollectionKit.isEmpty(blockPair.getData())){
            return BusinessKit.fail(response.getMsg());
        }
        if(exsitsPair.isFail() || CollectionKit.isEmpty(exsitsPair.getData())){
            return BusinessKit.fail(response.getMsg());
        }
        List<Long> addUsers = new ArrayList<>();
        users.forEach(u->{
            if (!blockPair.getData().contains(u) && !exsitsPair.getData().contains(u)){
                addUsers.add(u);
            }
        });
        if (CollectionKit.isEmpty(addUsers)){
            return BusinessKit.fail("no valid users");
        }
        List<GroupMember> groupMembers = new ArrayList<>();
        addUsers.forEach(u -> {
            GroupMember groupMember = new GroupMember()
                    .setUid(u)
                    .setRole(LARY.GROUP.ROLE.COMMON)
                    .setInviteUid(RequestContext.getLoginUID())
                    .setGroupId(groupId);
            groupMembers.add(groupMember);
        });
        saveBatch(groupMembers);
        return BusinessKit.ok(addUsers);
    }

    @Override
    public ResponsePair<Void> setAdmin(long groupId, long uid) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getRole, LARY.GROUP.ROLE.MANAGER)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> removeAdmin(long groupId, long uid) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getRole, LARY.GROUP.ROLE.COMMON)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid);
        return null;
    }

    @Override
    public ResponsePair<List<GroupMemberVO>> members(long groupId) {
        GroupMember operator = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, RequestContext.getLoginUID())
                .one();
        if (operator == null || operator.getIsDelete()
                || operator.getStatus() == LARY.STATUS.BLOCK) {
            return BusinessKit.fail("status error");
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
            return BusinessKit.fail("data empty");
        }
        List<GroupMemberVO> vos = new ArrayList<>(data.size());
        data.forEach(t->{
            GroupMemberVO vo = new GroupMemberVO();
            vos.add(vo.of(t));
        });
        return BusinessKit.ok(vos);
    }

    @Override
    public ResponsePair<Void> changeOwner(long groupId, long uid) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getRole, LARY.GROUP.ROLE.CREATOR)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid,uid);
        lambdaUpdate()
                .set(GroupMember::getRole, LARY.GROUP.ROLE.MANAGER)
                .eq(GroupMember::getGroupId,groupId)
                .eq(GroupMember::getUid, response.getData().getUid());
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> disband(long groupId) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getIsDelete,true)
                .eq(GroupMember::getGroupId,groupId);
        return BusinessKit.ok();
    }

    private ResponsePair<Void> join(long uid, long groupId, long inviteId){
        GroupMember operator = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
        if (operator != null && operator.getStatus() == LARY.STATUS.BLOCK) {
            return BusinessKit.fail("been blocked");
        }
        if (operator == null) {
            this.save(new GroupMember()
                    .setGroupId(groupId)
                    .setUid(uid)
                    .setRole(LARY.GROUP.ROLE.COMMON)
                    .setVerifyCode(UUIDKit.getUUID()));
        }else {
            lambdaUpdate()
                    .set(GroupMember::getIsDelete,false)
                    .eq(GroupMember::getGroupId, groupId)
                    .set(GroupMember::getInviteUid, inviteId)
                    .eq(GroupMember::getUid, uid);
        }
        return BusinessKit.ok();
    }

    public ResponsePair<GroupMember> checkRole(long groupId) {
        long operator = RequestContext.getLoginUID();
        GroupMember admin = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, operator)
                .one();
        if (admin == null) {
            return BusinessKit.fail("not group member");
        }
        if (admin.getRole() == LARY.GROUP.ROLE.COMMON) {
            return BusinessKit.fail("no privilege");
        }
        return BusinessKit.ok(admin);
    }

    @Override
    public ResponsePair<List<Long>> my(int role) {
        long uid = RequestContext.getLoginUID();
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getGroupId)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, role)
                .list();
        if (CollectionKit.isEmpty(data)){
            return BusinessKit.fail("data empty");
        }

        return BusinessKit.ok(data.stream().map(GroupMember::getUid).toList());
    }

    @Override
    public ResponsePair<Void> block(long groupId, long uid) {
        ResponsePair<GroupMember> response = checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(GroupMember::getStatus, LARY.STATUS.BLOCK)
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid);
        return BusinessKit.ok();
    }
}
