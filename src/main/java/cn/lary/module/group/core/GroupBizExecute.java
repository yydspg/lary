package cn.lary.module.group.core;

import cn.lary.core.dto.ResPair;
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
public class GroupBizExecute {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final GroupMemberSettingService groupMemberSettingService;

    /**
     * 创建群聊
     *
     * @param dto {@link CreateGroupDTO}
     * @return {@link CreateGroupVO}
     */
    public ResPair<CreateGroupVO> create(CreateGroupDTO dto) {
        return groupService.create(dto);
    }
 

    /**
     * 全体成员禁言
     * @param groupId g
     * @return ok
     */
    public ResPair<Void> forbidden(int groupId) {
        return groupService.forbidden(groupId);
    }

    /**
     * 查询群信息
     * @param groupId g
     * @return {@link GroupDetailVO}
     */
    public ResPair<GroupDetailVO> getGroup(int groupId) {
        return groupService.getGroup(groupId);
    }

    /**
     * 查询我的群聊,通过role
     * @param role 角色
     * @return {@link GroupVO}
     */
    public ResPair<List<GroupVO>> myGroups(int role) {
        return groupService.my(role);
    }
    /**
     * 修改群成员对群聊的设置<br>
     * 区别于群管理员对群本身的设置<br>
     * @param dto {@link GroupMemberSettingDTO}
     * @return ok
     */
    public ResPair<Void> setting(GroupMemberSettingDTO dto){
        return groupMemberSettingService.saveOrUpdate(dto);
    }

    /**
     * 获取群设置
     * @return {@link GroupMemberSettingVO}
     */
    public ResPair<List<GroupMemberSettingVO>> mySettings(){
        return groupMemberSettingService.my();
    }
    
    /**
     * 查询某种状态的用户
     * @param groupNo g
     * @param status s
     * @return ok
     */
    public ResPair<List<Integer>> getMembersWithStatus(int groupNo, int status){
        return groupMemberService.getMembersWithStatus(groupNo, status);
    }
    /**
     * 退出群聊
     * @param groupId g
     * @return ok
     */
    public ResPair<Void> quit(int groupId) {
        return groupMemberService.quit(groupId);
    }

    /**
     * 管理员强退
     * @param groupId g
     * @param uid u
     * @return ok
     */
    public ResPair<Void> quitByAdmin(int groupId,int uid){
        return groupMemberService.quitByAdmin(groupId,uid);
    }

    /**
     * 加入群聊
     * @param groupId g
     * @return ok
     */
    public ResPair<Void> join(int groupId) {
        return groupMemberService.join(groupId);
    }
    /**
     * 管理员邀请加入群聊
     * @param groupId g
     * @param uid  u
     * @return ok
     */
    public ResPair<Void> joinByAdmin(int groupId,int uid) {
        return groupMemberService.joinByAdmin(groupId,uid);
    }

    /**
     * 批量加群
     * @param groupId d
     * @param ids i
     * @return 正确加群的ids
     */
    public ResPair<Integer> multiJoinByAdmin(int groupId,List<Integer> ids){
        return groupMemberService.multiJoinByAdmin(groupId,ids);
    }

    /**
     * 设置用户为管理员
     * @param groupId g
     * @param uid 被设置的用户
     * @return ok
     */
    public ResPair<Void> setAdmin(int groupId,int uid){
        return groupMemberService.setAdmin(groupId,uid);
    }

    /**
     * 移除管理员
     * @param groupId g
     * @param uid u
     * @return ok
     */
    public ResPair<Void> removeAdmin(int groupId,int uid){
        return groupMemberService.removeAdmin(groupId,uid);
    }

    /**
     * 查询群成员
     * @param groupId g
     * @return {@link GroupMemberVO}
     */
    public ResPair<List<GroupMemberVO>> members(int groupId){
        return  groupMemberService.members(groupId);
    }
    /**
     * 转移群主
     * @param groupId g
     * @param uid 被转移用户
     * @return ok
     */
    public ResPair<Void> changeOwner(int groupId, int uid) {
        return groupMemberService.changeOwner(groupId, uid);
    }

    /**
     * 移除成员
     * @param groupId g
     * @return ok
     */
    public ResPair<Void> disband(int groupId) {
        return groupService.disband(groupId);
    }

    /**
     * 封禁用户
     * @param groupId g
     * @param uid u
     * @return ok
     */
    public ResPair<Void> block(int groupId,int uid){
        return groupMemberService.block(groupId,uid);
    }
}

