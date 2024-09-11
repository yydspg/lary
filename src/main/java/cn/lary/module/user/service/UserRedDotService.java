package cn.lary.module.user.service;

import cn.lary.module.user.entity.UserRedDot;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface UserRedDotService extends IService<UserRedDot> {
    UserRedDot getUserRedDot(String uid,String category);
}