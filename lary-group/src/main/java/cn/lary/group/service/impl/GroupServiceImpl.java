package cn.lary.group.service.impl;

import cn.lary.api.message.YutakMessageService;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.group.constant.GROUP;
import cn.lary.group.dto.GroupBuildDTO;
import cn.lary.group.entity.Group;
import cn.lary.group.entity.GroupMember;
import cn.lary.group.mapper.GroupMapper;
import cn.lary.group.service.GroupMemberService;
import cn.lary.group.service.GroupService;
import cn.lary.group.vo.CreateGroupVO;
import cn.lary.group.vo.GroupDetailVO;
import cn.lary.group.vo.GroupVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {


    private final GroupMemberService groupMemberService;
//    private final UserService userService;
    private final YutakMessageService messageService;


    @Override
    public boolean isReachCreateLimit(long uid, LocalDateTime now) {
        // TODO  :
        return false;
    }

    @Override
    public ResponsePair<CreateGroupVO> build(GroupBuildDTO dto) {
//        long creator = RequestContext.uid();
//        if (isReachCreateLimit(creator, LocalDateTime.now())) {
//            return BusinessKit.fail("reach create limit");
//        }
//        List<Long> members = CollectionKit.removeRepeat(dto.getMembers());
//        members.add(creator);
////        List<Long> users = userService.getValidUsers(members);
//        if (CollectionKit.isEmpty(users)) {
//            return BusinessKit.fail("valid users empty");
//        }
//        String groupName = dto.getName();
//        if (StringKit.isEmpty(groupName)) {
//            StringBuilder sb = new StringBuilder();
//            users.forEach(u -> {
//                sb.append(u).append(",");
//            });
//            groupName = sb.toString();
//        }
////        User creatorInfo = userService.lambdaQuery()
////                .select(User::getRole)
////                .eq(User::getUid, creator)
////                .one();
//        if (dto.getCategory() == GROUP.ROLE.MANAGER) {
//            if (creatorInfo == null || creatorInfo.getRole() == GROUP.ROLE.MANAGER) {
//                return BusinessKit.fail("inappropriate category");
//            }
//        }
//        Group group = new Group()
//                .setCreator(creator)
//                .setCategory(dto.getCategory())
//                .setName(groupName);
//        save(group);
//        List<GroupMember> groupMembers = new ArrayList<>();
//        users.forEach(u -> {
//            int role = GROUP.ROLE.COMMON;
//            if (u == creator) {
//                role = GROUP.ROLE.CREATOR;
//            }
//            GroupMember groupMember = new GroupMember()
//                    .setUid(u)
//                    .setRole(role)
//                    .setGid(group.getGid());
//            groupMembers.add(groupMember);
//        });
//        groupMemberService.saveBatch(groupMembers);
//        CreateGroupVO vo = new CreateGroupVO(group.getGid(), groupName, LocalDateTime.now());
//        messageService.send(new CreateGroupSuccessNotifyDTO(RequestContext.uid(), group.getGid(), users));
//        return BusinessKit.ok(vo);
        return null;
    }

    @Override
    public ResponsePair<Void> disband(long groupId) {
        ResponsePair<Void> response = groupMemberService.disband(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .eq(Group::getGid,groupId)
                .set(Group::getStatus, GROUP.STATUS.DISBAND)
                .update();
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> forbidden(long groupId) {
        ResponsePair<GroupMember> response = groupMemberService.checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .set(Group::getIsForbidden,true)
                .update();
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<GroupDetailVO> getGroup(long groupId) {
        Group group = lambdaQuery()
                .eq(Group::getGid, groupId)
                .one();
        if (group == null){
            return BusinessKit.fail("group not exist");
        }
        return BusinessKit.ok(new GroupDetailVO(group));
    }

    @Override
    public ResponsePair<List<GroupVO>> my(int role) {
        ResponsePair<List<Long>> response = groupMemberService.my(role);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        List<Group> data = lambdaQuery()
                .select(Group::getGid,Group::getGroupAvatar
                        ,Group::getGroupNum,Group::getName)
                .in(Group::getGid,response.getData())
                .list();
        if (data.isEmpty()){
            return BusinessKit.fail("data empty");
        }
        List<GroupVO> vos = data.stream()
                .map(GroupVO::new)
                .toList();
        return BusinessKit.ok(vos);
    }

}
