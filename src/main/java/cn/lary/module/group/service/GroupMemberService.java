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
     *
     * @param groupNo group id
     * @param status @Lary.Group.Status
     * @return block list uid
     */
    List<Integer> queryMemberWithStatus(int groupNo,int status);


}
