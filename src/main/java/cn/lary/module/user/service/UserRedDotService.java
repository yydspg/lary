package cn.lary.module.user.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.user.entity.UserRedDot;
import cn.lary.module.user.vo.UserRedDotVO;
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
public interface UserRedDotService extends IService<UserRedDot> {
    /**
     * 查询用户红点
     * @return {@link UserRedDotVO}
     */
    ResponsePair<List<UserRedDotVO>> redDots();

    /**
     * 清除某个策略下红点
     * @param category c
     * @return ok
     */
    ResponsePair<Void> clear(int category);

    /**
     * 增加用户红点数量
     * @param category c
     * @param amount a
     * @return ok
     */
    ResponsePair<Void> increment(int category,int amount);
}
