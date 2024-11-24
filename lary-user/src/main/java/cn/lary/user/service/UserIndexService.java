package cn.lary.user.service;

import cn.lary.user.dto.UidQueryDTO;
import cn.lary.user.entity.UserIndex;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
public interface UserIndexService extends IService<UserIndex> {

    /**
     * uid query
     * @return ok
     */
    long uid(UidQueryDTO dto);

}
