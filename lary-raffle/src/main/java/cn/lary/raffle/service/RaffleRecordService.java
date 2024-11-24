package cn.lary.raffle.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.raffle.dto.RaffleRecordPageQueryDTO;
import cn.lary.raffle.entity.RaffleRecord;
import cn.lary.raffle.vo.RaffleRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
public interface RaffleRecordService extends IService<RaffleRecord> {

        /**
         * 分页查询抽奖记录
         * @param dto {@link RaffleRecordPageQueryDTO}
         * @return {@link RaffleRecordVO}
         */
        ResponsePair<List<RaffleRecordVO>> my(RaffleRecordPageQueryDTO dto);
}
