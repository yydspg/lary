package cn.lary.raffle.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.raffle.dto.RaffleEventDTO;
import cn.lary.api.raffle.dto.RaffleRecordPageQueryDTO;
import cn.lary.raffle.service.RaffleEventService;
import cn.lary.raffle.service.RaffleInvolvedService;
import cn.lary.raffle.service.RaffleRecordService;
import cn.lary.api.raffle.vo.RaffleEventVO;
import cn.lary.api.raffle.vo.RaffleRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RaffleExecute {

    private final RaffleEventService raffleEventService;
    private final RaffleRecordService raffleRecordService;
    private final RaffleInvolvedService raffleInvolvedService;

    /**
     * 创建直播抽奖事件
     * @param dto {@link RaffleEventDTO}
     * @return OK
     */
    public ResponsePair<Void> raffle(RaffleEventDTO dto) {
        return raffleEventService.raffle(dto);
    }

    /**
     * 查询我的直播中奖记录
     * @param dto {@link RaffleRecordPageQueryDTO}
     * @return {@link RaffleRecordVO}
     */
    public ResponsePair<List<RaffleRecordVO>> my(RaffleRecordPageQueryDTO dto){
        return raffleRecordService.my(dto);
    }

    /**
     * 参加抽奖活动
     * @param toUid anchor uid
     * @return OK
     */
    public ResponsePair<Void> join(long toUid) {
        return raffleInvolvedService.join(toUid);
    }

    /**
     * 查看抽奖接口
     * @param toUid anchor uid
     * @return RaffleEvent
     */
    public ResponsePair<RaffleEventVO> info(long toUid) {
        return raffleEventService.info(toUid);
    }
}
