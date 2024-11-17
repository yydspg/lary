package cn.lary.module.user.service;

import cn.lary.module.user.dto.UidQueryDTO;
import cn.lary.module.user.entity.UserIndex;
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
