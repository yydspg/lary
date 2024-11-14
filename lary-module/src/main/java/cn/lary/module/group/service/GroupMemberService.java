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


    GroupMember build(GroupMember dto);
    /**
     * 查询某种状态的用户
     * @param groupNo g
     * @param status s
     * @return OK
     */
    ResponsePair<List<Long>> getMembersWithStatus(long groupNo, int status);
    /**
     * 退出群聊
     * @param groupId g
     * @return OK
     */
    ResponsePair<Void> quit(long groupId) ;

    /**
     * 管理员强退
     * @param groupId g
     * @param uid u
     * @return OK
     */
    ResponsePair<Void> quitByAdmin(long groupId, long uid);

    /**
     * 加入群聊
     * @param groupId g
     * @return OK
     */
    ResponsePair<Void> join(long groupId) ;
    /**
     * 管理员邀请加入群聊
     * @param groupId g
     * @param uid  u
     * @return OK
     */
    ResponsePair<Void> joinByAdmin(long groupId, long uid) ;

    /**
     * 批量加群
     * @param groupId d
     * @param ids i
     * @return 正确加群的ids
     */
    ResponsePair<List<Long>> multiJoinByAdmin(long groupId, List<Long> ids);

    /**
     * 设置用户为管理员
     * @param groupId g
     * @param uid 被设置的用户
     * @return OK
     */
    ResponsePair<Void> setAdmin(long groupId, long uid);

    /**
     * 移除管理员
     * @param groupId g
     * @param uid u
     * @return OK
     */
    ResponsePair<Void> removeAdmin(long groupId, long uid);

    /**
     * 查询群成员
     * @param groupId g
     * @return {@link GroupMemberVO}
     */
    ResponsePair<List<GroupMemberVO>> members(long groupId);
    /**
     * 转移群主
     * @param groupId g
     * @param uid 被转移用户
     * @return OK
     */
    ResponsePair<Void> changeOwner(long groupId, long uid) ;

    /**
     * 移除成员
     * @param groupId g
     * @return OK
     */
    ResponsePair<Void> disband(long groupId) ;

    /**
     * 检查是否权限足够
     * @param groupId g
     * @return GroupMember
     */
    ResponsePair<GroupMember> checkRole(long groupId) ;

    /**
     * 查询我的群聊
     * @param role 角色
     * @return {@link GroupDetailVO}
     */
    ResponsePair<List<Long>> my(int role);

    /**
     * 封禁用户
     * @param groupId g
     * @param uid u
     * @return OK
     */
    ResponsePair<Void> block(long groupId, long uid);
}
