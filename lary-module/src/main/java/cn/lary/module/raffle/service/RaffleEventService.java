package cn.lary.module.raffle.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.raffle.dto.RaffleEventDTO;
import cn.lary.module.raffle.entity.RaffleEvent;
import cn.lary.module.raffle.vo.RaffleEventVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
public interface RaffleEventService extends IService<RaffleEvent> {


   ResponsePair<Void> raffle(RaffleEventDTO dto);

   ResponsePair<RaffleEventVO> info(long toUid);
}
