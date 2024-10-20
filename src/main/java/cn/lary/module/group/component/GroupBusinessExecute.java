package cn.lary.module.group.component;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.dto.GroupMemberSettingDTO;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupMemberSettingService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.group.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBusinessExecute {

    private final GroupService groupService;

    /**
     * 创建群聊
     *
     * @param dto {@link CreateGroupDTO}
     * @return {@link CreateGroupVO}
     */
    public ResponsePair<CreateGroupVO> create(CreateGroupDTO dto) {
        return groupService.create(dto);
    }
 

    /**
     * 全体成员禁言
     * @param groupId g
     * @return ok
     */
    public ResponsePair<Void> forbidden(int groupId) {
        return groupService.forbidden(groupId);
    }

    /**
     * 查询群信息
     * @param groupId g
     * @return {@link GroupDetailVO}
     */
    public ResponsePair<GroupDetailVO> getGroup(int groupId) {
        return groupService.getGroup(groupId);
    }

    /**
     * 查询我的群聊,通过role
     * @param role 角色
     * @return {@link GroupVO}
     */
    public ResponsePair<List<GroupVO>> myGroups(int role) {
        return groupService.my(role);
    }

    /**
     * 移除成员
     * @param groupId g
     * @return ok
     */
    public ResponsePair<Void> disband(int groupId) {
        return groupService.disband(groupId);
    }


}

