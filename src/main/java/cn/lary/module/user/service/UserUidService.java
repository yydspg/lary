package cn.lary.module.user.service;

import cn.lary.module.user.entity.UserUid;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-09-21
 */
public interface UserUidService extends IService<UserUid> {

    String getUid();
}
