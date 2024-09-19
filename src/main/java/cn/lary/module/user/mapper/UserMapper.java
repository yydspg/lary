package cn.lary.module.user.mapper;

import cn.lary.module.user.vo.FriendCodeCheck;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.vo.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserShowInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface UserMapper extends BaseMapper<User> {
    List<UserBaseVO> selectBaseByIDs(@Param("uids") List<String> uids);

    UserBaseVO selectBase(@Param("uid")String uid);
    UserBasicInfo selectBasicInfo(@Param("uid")String uid);
    FriendCodeCheck checkWithCode(@Param("code") String code);

    FriendCodeCheck checkWithQRCode(@Param("code") String code);
    List<UserShowInfo> selectUserShowInfo(@Param("uids") List<String> uid);
}
