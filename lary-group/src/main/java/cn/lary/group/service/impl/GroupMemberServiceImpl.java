package cn.lary.group.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.UUIDKit;
import cn.lary.group.constant.GROUP;
import cn.lary.group.entity.GroupMember;
import cn.lary.group.mapper.GroupMemberMapper;
import cn.lary.group.service.GroupMemberService;
import cn.lary.group.vo.GroupMemberVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {

//    private final UserService userService;
    private final LaryIDBuilder builder;
    private final TransactionTemplate transactionTemplate;
    
    @Override
    public GroupMember build(GroupMember dto) {
        dto.setMid(builder.next());
        save(dto);
        return dto;
    }

    @Override
    public ResponsePair<List<Long>> getMembersWithStatus(long groupNo, int status) {
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGid, groupNo)
                .eq(GroupMember::getStatus, status)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return BusinessKit.ok(Collections.emptyList());
        }
        List<Long> vos = data.stream()
                .map(GroupMember::getUid)
                .toList();
        return BusinessKit.ok(vos);
    }

    @Override
    public ResponsePair<Void> quit(long gid) {
        return transactionTemplate.execute(status -> {
            long uid = RequestContext.uid();
            GroupMember member = lambdaQuery()
                    .select(GroupMember::getUid)
                    .eq(GroupMember::getGid,gid)
                    .eq(GroupMember::getUid,uid)
                    .one();
            if (member == null) {
                return BusinessKit.fail("not group member");
            }
            if (member.getRole() == GROUP.ROLE.CREATOR) {
                return BusinessKit.fail("creator can not quit");
            }
            lambdaUpdate()
                    .set(GroupMember::getStatus,GROUP.MEMBER.STATUS.QUIT)
                    .eq(GroupMember::getGid, gid)
                    .eq(GroupMember::getUid,uid);
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> quitByAdmin(long gid, long uid) {
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()){
                return BusinessKit.fail(response.getMsg());
            }
            lambdaUpdate()
                    .set(GroupMember::getStatus,GROUP.MEMBER.STATUS.BLOCK)
                    .eq(GroupMember::getGid, gid)
                    .eq(GroupMember::getUid, uid);
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> join(long gid) {
        return join(RequestContext.uid(),gid,0);
    }

    @Override
    public ResponsePair<Void> joinByAdmin(long gid, long uid) {
        ResponsePair<GroupMember> response = checkRole(gid);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        return join(uid,gid,response.getData().getUid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponsePair<List<Long>> multiJoinByAdmin(long gid, List<Long> ids) {
        if (CollectionKit.isEmpty(ids)){
            return BusinessKit.fail("user empty");
        }
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()){
                return BusinessKit.fail(response.getMsg());
            }
            List<Long> members = CollectionKit.removeRepeat(ids);
//            List<Long> users = userService.getValidUsers(members);
//            if (CollectionKit.isEmpty(users)) {
//                return BusinessKit.fail("valid users empty");
//            }
            ResponsePair<List<Long>> blockPair = getMembersWithStatus(gid, GROUP.MEMBER.STATUS.BLOCK);
            ResponsePair<List<Long>> existsPair = getMembersWithStatus(gid, GROUP.MEMBER.STATUS.COMMON);

            if(blockPair.isFail() || CollectionKit.isEmpty(blockPair.getData())){
                return BusinessKit.fail(response.getMsg());
            }
            if(existsPair.isFail() || CollectionKit.isEmpty(existsPair.getData())){
                return BusinessKit.fail(response.getMsg());
            }
//            List<Long> addUsers = users.stream()
//                    .filter(u -> !blockPair.getData().contains(u) && !existsPair.getData().contains(u))
//                    .toList();
//            if (CollectionKit.isEmpty(addUsers)){
//                return BusinessKit.fail("no valid users");
//            }
//            List<GroupMember> groupMembers = addUsers.stream()
//                    .map(u->new GroupMember()
//                            .setUid(u)
//                            .setRole(GROUP.ROLE.COMMON)
//                            .setInviteUid(RequestContext.uid())
//                            .setGid(gid)
//                    ).toList();
//            saveBatch(groupMembers);
//            return BusinessKit.ok(addUsers);
            return BusinessKit.ok(members);
        });
    }

    @Override
    public ResponsePair<Void> setAdmin(long gid, long uid) {
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()) {
                return BusinessKit.fail(response.getMsg());
            }
            lambdaUpdate()
                    .set(GroupMember::getRole, GROUP.ROLE.MANAGER)
                    .eq(GroupMember::getGid, gid)
                    .eq(GroupMember::getUid, uid)
                    .update();
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> removeAdmin(long gid, long uid) {
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()){
                return BusinessKit.fail(response.getMsg());
            }
            lambdaUpdate()
                    .set(GroupMember::getRole, GROUP.ROLE.COMMON)
                    .eq(GroupMember::getGid,gid)
                    .eq(GroupMember::getUid,uid)
                    .update();
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<List<GroupMemberVO>> members(long gid) {
        GroupMember operator = lambdaQuery()
                .select(GroupMember::getStatus)
                .eq(GroupMember::getGid, gid)
                .eq(GroupMember::getUid, RequestContext.uid())
                .one();
        if (operator == null || operator.getStatus() == GROUP.MEMBER.STATUS.BLOCK) {
            return BusinessKit.fail("status error");
        }
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getStatus,GroupMember::getNickname
                        ,GroupMember::getName,GroupMember::getAvatar
                        ,GroupMember::getUid,GroupMember::getRole)
                .eq(GroupMember::getGid, gid)
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
    public ResponsePair<Void> changeOwner(long gid, long uid) {
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()){
                return BusinessKit.fail(response.getMsg());
            }
            lambdaUpdate()
                    .set(GroupMember::getRole, GROUP.ROLE.CREATOR)
                    .eq(GroupMember::getGid,gid)
                    .eq(GroupMember::getUid,uid)
                    .update();
            lambdaUpdate()
                    .set(GroupMember::getRole, GROUP.ROLE.MANAGER)
                    .eq(GroupMember::getGid,gid)
                    .eq(GroupMember::getUid, response.getData().getUid())
                    .update();
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> disband(long gid) {
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()){
                return BusinessKit.fail(response.getMsg());
            }
            lambdaUpdate()
                    .set(GroupMember::getStatus,GROUP.MEMBER.STATUS.DISBAND)
                    .eq(GroupMember::getGid,gid);
            return BusinessKit.ok();
        });
    }

    private ResponsePair<Void> join(long uid, long gid, long inviteId){
        return transactionTemplate.execute(status -> {
            GroupMember operator = lambdaQuery()
                    .select(GroupMember::getUid)
                    .eq(GroupMember::getGid, gid)
                    .eq(GroupMember::getUid, uid)
                    .one();
            if (operator != null && operator.getStatus() == GROUP.MEMBER.STATUS.BLOCK) {
                return BusinessKit.fail("been blocked");
            }
            if (operator == null) {
                this.save(new GroupMember()
                        .setGid(gid)
                        .setUid(uid)
                        .setRole(GROUP.ROLE.COMMON)
                        .setVerifyCode(UUIDKit.getUUID()));
            }else {
                lambdaUpdate()
                        .set(GroupMember::getStatus,GROUP.MEMBER.STATUS.COMMON)
                        .set(GroupMember::getInviteUid, inviteId)
                        .eq(GroupMember::getGid, gid)
                        .eq(GroupMember::getUid, uid);
            }
            return BusinessKit.ok();
        });
    }

    public ResponsePair<GroupMember> checkRole(long gid) {
        long operator = RequestContext.uid();
        GroupMember admin = lambdaQuery()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGid, gid)
                .eq(GroupMember::getUid, operator)
                .one();
        if (admin == null) {
            return BusinessKit.fail("not group member");
        }
        if (admin.getRole() == GROUP.ROLE.COMMON) {
            return BusinessKit.fail("no privilege");
        }
        return BusinessKit.ok(admin);
    }

    @Override
    public ResponsePair<List<Long>> my(int role) {
        long uid = RequestContext.uid();
        List<GroupMember> data = lambdaQuery()
                .select(GroupMember::getGid)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, role)
                .list();
        if (CollectionKit.isEmpty(data)){
            return BusinessKit.fail("data empty");
        }

        return BusinessKit.ok(data.stream()
                .map(GroupMember::getUid)
                .toList());
    }

    @Override
    public ResponsePair<Void> block(long gid, long uid) {
        return transactionTemplate.execute(status -> {
            ResponsePair<GroupMember> response = checkRole(gid);
            if (response.isFail()){
                return BusinessKit.fail(response.getMsg());
            }
            lambdaUpdate()
                    .set(GroupMember::getStatus,GROUP.MEMBER.STATUS.BLOCK)
                    .eq(GroupMember::getGid, gid)
                    .eq(GroupMember::getUid, uid);
            return BusinessKit.ok();
        });
    
    }
}
