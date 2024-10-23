package cn.lary.module.group.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.group.dto.GroupMemberSettingDTO;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupMemberSettingService;
import cn.lary.module.group.vo.GroupMemberSettingVO;
import cn.lary.module.group.vo.GroupMemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupMemberBusinessExecute {

    private final GroupMemberService groupMemberService;
    private final GroupMemberSettingService groupMemberSettingService;

  /**
     * 修改群成员对群聊的设置<br>
     * 区别于群管理员对群本身的设置<br>
     * @param dto {@link GroupMemberSettingDTO}
     * @return ok
     */
    public ResponsePair<Void> setting(GroupMemberSettingDTO dto){
        return groupMemberSettingService.saveOrUpdate(dto);
    }

    /**
     * 获取群设置
     * @return {@link GroupMemberSettingVO}
     */
    public ResponsePair<List<GroupMemberSettingVO>> mySettings(){
        return groupMemberSettingService.my();
    }

    /**
     * 查询某种状态的用户
     * @param groupNo g
     * @param status s
     * @return ok
     */
    public ResponsePair<List<Integer>> getMembersWithStatus(int groupNo, int status){
        return groupMemberService.getMembersWithStatus(groupNo, status);
    }
    /**
     * 退出群聊
     * @param groupId g
     * @return ok
     */
    public ResponsePair<Void> quit(int groupId) {
        return groupMemberService.quit(groupId);
    }

    /**
     * 管理员强退
     * @param groupId g
     * @param uid u
     * @return ok
     */
    public ResponsePair<Void> quitByAdmin(int groupId, int uid){
        return groupMemberService.quitByAdmin(groupId,uid);
    }

    /**
     * 加入群聊
     * @param groupId g
     * @return ok
     */
    public ResponsePair<Void> join(int groupId) {
        return groupMemberService.join(groupId);
    }
    /**
     * 管理员邀请加入群聊
     * @param groupId g
     * @param uid  u
     * @return ok
     */
    public ResponsePair<Void> joinByAdmin(int groupId, int uid) {
        return groupMemberService.joinByAdmin(groupId,uid);
    }

    /**
     * 批量加群
     * @param groupId d
     * @param ids i
     * @return 正确加群的ids
     */
    public ResponsePair<Integer> multiJoinByAdmin(int groupId, List<Integer> ids){
        return groupMemberService.multiJoinByAdmin(groupId,ids);
    }

    /**
     * 设置用户为管理员
     * @param groupId g
     * @param uid 被设置的用户
     * @return ok
     */
    public ResponsePair<Void> setAdmin(int groupId, int uid){
        return groupMemberService.setAdmin(groupId,uid);
    }

    /**
     * 移除管理员
     * @param groupId g
     * @param uid u
     * @return ok
     */
    public ResponsePair<Void> removeAdmin(int groupId, int uid){
        return groupMemberService.removeAdmin(groupId,uid);
    }

    /**
     * 查询群成员
     * @param groupId g
     * @return {@link GroupMemberVO}
     */
    public ResponsePair<List<GroupMemberVO>> members(int groupId){
        return  groupMemberService.members(groupId);
    }
    /**
     * 转移群主
     * @param groupId g
     * @param uid 被转移用户
     * @return ok
     */
    public ResponsePair<Void> changeOwner(int groupId, int uid) {
        return groupMemberService.changeOwner(groupId, uid);
    }

    /**
     * 封禁用户
     * @param groupId g
     * @param uid u
     * @return ok
     */
    public ResponsePair<Void> block(int groupId, int uid){
        return groupMemberService.block(groupId,uid);
    }

}
