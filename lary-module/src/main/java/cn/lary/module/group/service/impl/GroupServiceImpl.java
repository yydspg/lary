package cn.lary.module.group.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.DateKit;
import cn.lary.common.kit.StringKit;
import cn.lary.external.wk.api.WKMessageService;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.mapper.GroupMapper;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.group.vo.CreateGroupVO;
import cn.lary.module.group.vo.GroupDetailVO;
import cn.lary.module.group.vo.GroupVO;
import cn.lary.module.message.dto.group.CreateGroupSuccessNotifyDTO;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final UserService userService;

    // component

    // external
    private final WKMessageService wkMessageService;

    @Override
    public boolean isReachCreateLimit(long uid, LocalDateTime now) {
        LocalDateTime startOfDay = DateKit.getStartOfDay(now);
        LocalDateTime endOfDay = DateKit.getEndOfDay(now);
        // TODO  :  这里需要实现动态配置
        return Math.toIntExact(lambdaQuery()
                .eq(Group::getCreator, uid)
                .ge(Group::getCreateAt, startOfDay)
                .le(Group::getCreateAt, endOfDay)
                .count()) > 0;
    }

    @Override
    public ResponsePair<CreateGroupVO> create(CreateGroupDTO dto) {
        long creator = RequestContext.uid();
        if (isReachCreateLimit(creator, LocalDateTime.now())) {
            return BusinessKit.fail("reach create limit");
        }
        List<Long> members = CollectionKit.removeRepeat(dto.getMembers());
        members.add(creator);
        List<Long> users = userService.getValidUsers(members);
        if (CollectionKit.isEmpty(users)) {
            return BusinessKit.fail("valid users empty");
        }
        String groupName = dto.getName();
        if (StringKit.isEmpty(groupName)) {
            StringBuilder sb = new StringBuilder();
            users.forEach(u -> {
                sb.append(u).append(",");
            });
            groupName = sb.toString();
        }
        User creatorInfo = userService.lambdaQuery()
                .select(User::getRole)
                .eq(User::getUid, creator)
                .eq(User::getIsDelete, false)
                .one();
        if (dto.getCategory() == LARY.GROUP.ROLE.MANAGER) {
            if (creatorInfo == null || creatorInfo.getRole() == LARY.GROUP.ROLE.MANAGER) {
                return BusinessKit.fail("inappropriate category");
            }
        }
        Group group = new Group()
                .setCreator(creator)
                .setCategory(dto.getCategory())
                .setName(groupName);
        save(group);
        List<GroupMember> groupMembers = new ArrayList<>();
        users.forEach(u -> {
            int role = LARY.GROUP.ROLE.COMMON;
            if (u == creator) {
                role = LARY.GROUP.ROLE.CREATOR;
            }
            GroupMember groupMember = new GroupMember()
                    .setUid(u)
                    .setRole(role)
                    .setGroupId(group.getGroupId());
            groupMembers.add(groupMember);
        });
        groupMemberService.saveBatch(groupMembers);
        CreateGroupVO vo = new CreateGroupVO(group.getGroupId(), groupName, LocalDateTime.now());
        Response<Void> response = wkMessageService.send(new CreateGroupSuccessNotifyDTO()
                .build(RequestContext.uid(), group.getGroupId(), users));
        if (!response.isSuccessful()) {
            return BusinessKit.fail(response.message());
        }
        return BusinessKit.ok(vo);
    }

    @Override
    public ResponsePair<Void> disband(long groupId) {
        ResponsePair<Void> response = groupMemberService.disband(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate()
                .eq(Group::getGroupId,groupId)
                .set(Group::getIsDelete,true);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> forbidden(long groupId) {
        ResponsePair<GroupMember> response = groupMemberService.checkRole(groupId);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        lambdaUpdate().set(Group::getIsForbidden,true);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<GroupDetailVO> getGroup(long groupId) {
        Group group = lambdaQuery().eq(Group::getGroupId, groupId).one();
        if (group == null){
            return BusinessKit.fail("group not exist");
        }
        return BusinessKit.ok(new GroupDetailVO().of(group));
    }

    @Override
    public ResponsePair<List<GroupVO>> my(int role) {
        ResponsePair<List<Long>> response = groupMemberService.my(role);
        if (response.isFail()){
            return BusinessKit.fail(response.getMsg());
        }
        List<Group> data = lambdaQuery()
                .select(Group::getGroupId)
                .select(Group::getName)
                .select(Group::getGroupNum)
                .select(Group::getGroupAvatar)
                .in(Group::getGroupId,response.getData())
                .list();
        if (data.isEmpty()){
            return BusinessKit.fail("data empty");
        }
        List<GroupVO> vos = new ArrayList<>();
        data.forEach(d ->{
            vos.add(new GroupVO().of(d));
        });
        return BusinessKit.ok(vos);
    }

}
