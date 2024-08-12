package cn.lary.module.user.mapper;

import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserBase;
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
    List<UserBaseRes> queryBaseByIDs(@Param("uids") List<String> uids);
}
