package cn.lary.raffle.dubbo;

import cn.lary.api.raffle.YutakRaffleService;
import cn.lary.raffle.service.RaffleEventService;
import cn.lary.raffle.service.RaffleInvolvedService;
import cn.lary.raffle.service.RaffleRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DubboService
public class YutakRaffleServiceImpl implements YutakRaffleService {
    private RaffleEventService raffleEventService;
    private RaffleInvolvedService raffleInvolvedService;
    private RaffleRecordService raffleRecordService;



}
