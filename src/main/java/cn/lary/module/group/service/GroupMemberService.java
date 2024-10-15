package cn.lary.module.group.service;

import cn.lary.core.dto.ResPair;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.vo.GroupMemberVO;
import cn.lary.module.group.vo.GroupDetailVO;
import cn.lary.module.group.vo.GroupVO;
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
    ResPair<List<Integer>> getMembersWithStatus(int groupNo,int status);
    /**
     * 退出群聊
     * @param groupId g
     * @return ok
     */
    ResPair<Void> quit(int groupId) ;

    /**
     * 管理员强退
     * @param groupId g
     * @param uid u
     * @return ok
     */
    ResPair<Void> quitByAdmin(int groupId,int uid);

    /**
     * 加入群聊
     * @param groupId g
     * @return ok
     */
    ResPair<Void> join(int groupId) ;
    /**
     * 管理员邀请加入群聊
     * @param groupId g
     * @param uid  u
     * @return ok
     */
    ResPair<Void> joinByAdmin(int groupId,int uid) ;

    /**
     * 批量加群
     * @param groupId d
     * @param ids i
     * @return 正确加群的ids
     */
    ResPair<Integer> multiJoinByAdmin(int groupId,List<Integer> ids);

    /**
     * 设置用户为管理员
     * @param groupId g
     * @param uid 被设置的用户
     * @return ok
     */
    ResPair<Void> setAdmin(int groupId,int uid);

    /**
     * 移除管理员
     * @param groupId g
     * @param uid u
     * @return ok
     */
    ResPair<Void> removeAdmin(int groupId,int uid);

    /**
     * 查询群成员
     * @param groupId g
     * @return {@link GroupMemberVO}
     */
    ResPair<List<GroupMemberVO>> members(int groupId);
    /**
     * 转移群主
     * @param groupId g
     * @param uid 被转移用户
     * @return ok
     */
    ResPair<Void> changeOwner(int groupId, int uid) ;

    /**
     * 移除成员
     * @param groupId g
     * @return ok
     */
    ResPair<Void> disband(int groupId) ;

    /**
     * 检查是否权限足够
     * @param groupId g
     * @return GroupMember
     */
    ResPair<GroupMember> checkRole(int groupId) ;

    /**
     * 查询我的群聊
     * @param role 角色
     * @return {@link GroupDetailVO}
     */
    ResPair<List<Integer>> my(int role);

    /**
     * 封禁用户
     * @param groupId g
     * @param uid u
     * @return ok
     */
    ResPair<Void> block(int groupId,int uid);
}
