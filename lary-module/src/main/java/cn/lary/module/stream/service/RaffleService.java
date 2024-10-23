package cn.lary.module.stream.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.stream.dto.RaffleDTO;
import cn.lary.module.stream.entity.Raffle;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
public interface RaffleService extends IService<Raffle> {
    /**
     * 创建抽奖活动
     * @param dto {@link RaffleDTO}
     * @return ok
     */
    ResponsePair<Void> raffle(RaffleDTO dto);

}
