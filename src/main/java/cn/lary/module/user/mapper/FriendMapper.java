package cn.lary.module.user.mapper;

import cn.lary.module.user.dto.res.FriendBase;
import cn.lary.module.user.dto.res.FriendCodeCheck;
import cn.lary.module.user.entity.Friend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface FriendMapper extends BaseMapper<Friend> {

    FriendCodeCheck checkWithCode(@Param("code") String code);
}
