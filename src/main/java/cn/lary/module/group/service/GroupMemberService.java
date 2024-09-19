package cn.lary.module.group.service;

import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.user.vo.FriendCodeCheck;
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
     *  see return
     * @param uid account which add member to this group
     * @param groupNo group id
     * @return check if this account is manager account or creator
     */
    boolean isCreatorOrManager(String uid, String groupNo,String creator);

    /**
     * check current uid if is the member
     * @param uid uid
     * @param groupNo group id
     * @return bool
     */
    boolean isMember(String uid, String groupNo);

    /**
     *
     * @param groupNo group id
     * @param status @Lary.Group.Status
     * @return block list uid
     */
    List<String> queryMemberWithStatus(String groupNo,int status);

    /**
     * jude this member has been deleted
     * @param groupNo group id
     * @param uid user id
     * @return see before
     */
    boolean isDeletedMember(String groupNo,String uid);

    /**
     * update user deleted to false
     * @param groupNo  group id
     * @param uid user id
     */
    void recoveryMember(String groupNo,String uid);

    /**
     *
     * @param groupNo groupNo
     * @return member count
     */
    long queryMemberCount(String groupNo);

    List<String> queryMemberWithLimit(String groupNo,long limit);

    FriendCodeCheck checkByCode(String code);
    GroupMember getMemberByVerCode(String vercode);

}
