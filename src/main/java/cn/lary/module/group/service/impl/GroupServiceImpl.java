package cn.lary.module.group.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.kit.DateKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.Lary;
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
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public boolean checkWhetherReachCreateLimit(int uid, LocalDateTime now) {
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
    public ResPair<CreateGroupVO> create(CreateGroupDTO dto) {
        int creator = RequestContext.getLoginUID();
        List<Integer> members = CollectionKit.removeRepeat(dto.getMembers());
        members.add(creator);
        List<Integer> users = userService.getValidUsers(members);
        String groupName = dto.getName();
        if (StringKit.isEmpty(groupName)) {
            StringBuilder sb = new StringBuilder();
            users.forEach(u -> {
                sb.append(u).append(",");
            });
            groupName = sb.toString();
        }
        User creatorInfo = userService.lambdaQuery()
                .eq(User::getUid, creator)
                .eq(User::getDeleted, false)
                .one();
        if (dto.getCategory() == Lary.Group.Category.stream) {
            if (creatorInfo == null || !creatorInfo.getIsAnchor()) {
                return BizKit.fail("inappropriate category");
            }
        }
        Group group = new Group()
                .setCreator(creator)
                .setCategory(dto.getCategory())
                .setName(groupName);
        save(group);
        List<GroupMember> groupMembers = new ArrayList<>();
        users.forEach(u -> {
            int role = Lary.Group.Role.common;
            if (u == creator) {
                role = Lary.Group.Role.creator;
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
                .build(RequestContext.getLoginUID(), group.getGroupId(), users));
        if (!response.isSuccessful()) {
            return BizKit.fail(response.message());
        }
        return BizKit.ok(vo);
    }

    @Override
    public ResPair<Void> disband(int groupId) {
        ResPair<Void> res = groupMemberService.disband(groupId);
        if (!res.isOk()){
            return res;
        }
        lambdaUpdate()
                .eq(Group::getGroupId,groupId)
                .set(Group::getIsDelete,true);
        return BizKit.ok();
    }

    @Override
    public ResPair<Void> forbidden(int groupId) {
        ResPair<GroupMember> res = groupMemberService.checkRole(groupId);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        lambdaUpdate().set(Group::getIsForbidden,true);
        return BizKit.ok();
    }

    @Override
    public ResPair<GroupDetailVO> getGroup(int groupId) {
        Group group = lambdaQuery().eq(Group::getGroupId, groupId).one();
        if (group == null){
            return BizKit.fail("group not exist");
        }
        return BizKit.ok(new GroupDetailVO().of(group));
    }

    @Override
    public ResPair<List<GroupVO>> my(int role) {
        ResPair<List<Integer>> res = groupMemberService.my(role);
        if (!res.isOk()){
            return BizKit.fail(res.getMsg());
        }
        List<Group> data = lambdaQuery()
                .select(Group::getGroupId)
                .select(Group::getName)
                .select(Group::getGroupNum)
                .select(Group::getGroupAvatar)
                .in(Group::getGroupId,res.getData())
                .list();
        if (data.isEmpty()){
            return BizKit.fail("data empty");
        }
        List<GroupVO> vos = new ArrayList<>();
        data.forEach(d ->{
            vos.add(new GroupVO().of(d));
        });
        return BizKit.ok(vos);
    }


}
