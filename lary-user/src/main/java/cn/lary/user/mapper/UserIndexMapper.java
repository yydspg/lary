package cn.lary.user.mapper;

import cn.lary.api.user.dto.UidQueryDTO;
import cn.lary.api.user.entity.UserIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
public interface UserIndexMapper extends BaseMapper<UserIndex> {

    long getUid(UidQueryDTO dto);
}
