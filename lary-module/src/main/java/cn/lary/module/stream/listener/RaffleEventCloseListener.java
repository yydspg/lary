package cn.lary.module.stream.listener;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.CollectionKit;
import cn.lary.external.wk.constant.WK;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.dto.RaffleCloseMessage;
import cn.lary.module.stream.entity.Raffle;
import cn.lary.module.stream.service.RaffleService;
import cn.lary.module.wallet.dto.BatchOutcomeTransferDTO;
import cn.lary.module.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "lary",consumerGroup = "springboot_consumer_group")
public class RaffleEventCloseListener implements RocketMQListener<RaffleCloseMessage> {

    private final RedisCache redisCache;
    private final EventService eventService;
    private final KVBuilder kvBuilder;
    private final WalletService walletService;
    private final RaffleService raffleService;

    @Override
    public void onMessage(RaffleCloseMessage message) {

        log.info("RaffleEventCloseListener receive message:{}", message.toString());
        int uid = message.getUid();
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (map == null) {
            log.error("raffle close event error,no live info,uid:{}",uid);
            return;
        }
        Map<Object, Object> data = redisCache.getHash(kvBuilder.raffleK(uid));
        if (data == null) {
            log.error("raffle close event error,no raffle info,uid:{}",uid);
            return;
        }
        int type = Integer.parseInt(map.get("type").toString());
        RaffleCacheDTO raffleCache = RaffleCacheDTO.of(data);
        Long size = redisCache.getListSize(kvBuilder.raffleListK(uid));
        if (size == null) {
            log.error("raffle close event error,no list info,uid:{}",uid);
            return;
        }
        List<String> collects = new ArrayList<>();
        if (size > 0 && size < 1000) {
            collects = redisCache.getRangeList(kvBuilder.raffleListK(uid), 0, size-1);
        }else {
            //need segment
            long batch = size / 4;
            for (long i = 0; i < size; i+=batch) {
                long end = Math.min(i+batch-1, size-1);
                collects.addAll(redisCache.getRangeList(kvBuilder.raffleListK(uid), i, end));
            }
        }
        Set<Integer> randomIndex = CollectionKit.generateUniqueRandomNumbers(0, Math.toIntExact(size - 1), raffleCache.getNum());
        List<Integer> uids = new ArrayList<>();
        for (Integer index : randomIndex) {
            uids.add(Integer.parseInt( collects.get(index)));
        }
        if (type == LARY.Raffle.inner) {
            BatchOutcomeTransferDTO dto = new BatchOutcomeTransferDTO().setType(type)
                    .setUid(uid)
                    .setAmount(raffleCache.getCost())
                    .setRecipients(uids)
                    .setChannelId(message.getStreamId())
                    .setChannelType(WK.CHANNEL.TYPE.STREAM)
                    .setTotalAmount(raffleCache.getTotalAmount());
            ResponsePair<Void> response = walletService.batchOutcomeTransfer(dto);
            if (response.isFail()) {
                log.error("batch outcome transfer error,uid:{},raffleId:{},reason:{}",uid,raffleCache.getId(),response.getMsg());
                return;
            }
        }
        raffleService.lambdaUpdate()
                .eq(Raffle::getId,message.getRaffleId())
                .set(Raffle::getIsSync,true)
                .set(Raffle::getRecipients,uids);
        redisCache.delete(kvBuilder.raffleListK(uid));
        redisCache.delete(kvBuilder.raffleK(uid));
        eventService.commit(message.getEventId());
        log.info("raffle close event success,uid:{},raffleId:{},streamId:{}"
                ,uid,message.getRaffleId(),message.getStreamId());
    }
}


