package cn.lary.group.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.group.dto.GroupBuildDTO;
import cn.lary.group.service.GroupService;
import cn.lary.group.vo.CreateGroupVO;
import cn.lary.group.vo.GroupDetailVO;
import cn.lary.group.vo.GroupVO;
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
     * @param dto {@link GroupBuildDTO}
     * @return {@link CreateGroupVO}
     */
    public ResponsePair<CreateGroupVO> build(GroupBuildDTO dto) {
        return groupService.build(dto);
    }
 

    /**
     * 全体成员禁言
     * @param groupId g
     * @return OK
     */
    public ResponsePair<Void> forbidden(long groupId) {
        return groupService.forbidden(groupId);
    }

    /**
     * 查询群信息
     * @param groupId g
     * @return {@link GroupDetailVO}
     */
    public ResponsePair<GroupDetailVO> getGroup(long groupId) {
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
     * @return OK
     */
    public ResponsePair<Void> disband(long groupId) {
        return groupService.disband(groupId);
    }


}

