package cn.lary.module.group.mapper;

import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.user.vo.FriendCodeCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface GroupMemberMapper extends BaseMapper<GroupMember> {


    FriendCodeCheck checkWithCode(String code);
}
