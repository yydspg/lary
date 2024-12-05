package cn.lary.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.user.entity.UserRedDot;
import cn.lary.api.user.vo.UserRedDotVO;
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


    UserRedDot build(UserRedDot dto);
    /**
     * 查询用户红点
     * @return {@link UserRedDotVO}
     */
    ResponsePair<List<UserRedDotVO>> redDots();

    /**
     * 清除某个策略红点
     * @param category c
     * @return OK
     */
    ResponsePair<Void> clear(int category);

    /**
     * 增加用户红点数量
     * @param category c
     * @param amount a
     * @return OK
     */
    ResponsePair<Void> increment(int category,int amount);
}
