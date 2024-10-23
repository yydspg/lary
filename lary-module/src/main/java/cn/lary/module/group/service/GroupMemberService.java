package cn.lary.module.group.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.vo.GroupDetailVO;
import cn.lary.module.group.vo.GroupMemberVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface GroupMemberService extends IService<GroupMember> {



    /**
     * 查询某种状态的用户
     * @param groupNo g
     * @param status s
     * @return ok
     */
    ResponsePair<List<Integer>> getMembersWithStatus(int groupNo, int status);
    /**
     * 退出群聊
     * @param groupId g
     * @return ok
     */
    ResponsePair<Void> quit(int groupId) ;

    /**
     * 管理员强退
     * @param groupId g
     * @param uid u
     * @return ok
     */
    ResponsePair<Void> quitByAdmin(int groupId, int uid);

    /**
     * 加入群聊
     * @param groupId g
     * @return ok
     */
    ResponsePair<Void> join(int groupId) ;
    /**
     * 管理员邀请加入群聊
     * @param groupId g
     * @param uid  u
     * @return ok
     */
    ResponsePair<Void> joinByAdmin(int groupId, int uid) ;

    /**
     * 批量加群
     * @param groupId d
     * @param ids i
     * @return 正确加群的ids
     */
    ResponsePair<Integer> multiJoinByAdmin(int groupId, List<Integer> ids);

    /**
     * 设置用户为管理员
     * @param groupId g
     * @param uid 被设置的用户
     * @return ok
     */
    ResponsePair<Void> setAdmin(int groupId, int uid);

    /**
     * 移除管理员
     * @param groupId g
     * @param uid u
     * @return ok
     */
    ResponsePair<Void> removeAdmin(int groupId, int uid);

    /**
     * 查询群成员
     * @param groupId g
     * @return {@link GroupMemberVO}
     */
    ResponsePair<List<GroupMemberVO>> members(int groupId);
    /**
     * 转移群主
     * @param groupId g
     * @param uid 被转移用户
     * @return ok
     */
    ResponsePair<Void> changeOwner(int groupId, int uid) ;

    /**
     * 移除成员
     * @param groupId g
     * @return ok
     */
    ResponsePair<Void> disband(int groupId) ;

    /**
     * 检查是否权限足够
     * @param groupId g
     * @return GroupMember
     */
    ResponsePair<GroupMember> checkRole(int groupId) ;

    /**
     * 查询我的群聊
     * @param role 角色
     * @return {@link GroupDetailVO}
     */
    ResponsePair<List<Integer>> my(int role);

    /**
     * 封禁用户
     * @param groupId g
     * @param uid u
     * @return ok
     */
    ResponsePair<Void> block(int groupId, int uid);
}
