package cn.lary.module.raffle.listener;

import cn.lary.module.common.service.EventService;
import cn.lary.module.cache.component.RaffleCacheComponent;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.raffle.entity.RaffleEvent;
import cn.lary.module.raffle.entity.RaffleRecord;
import cn.lary.module.raffle.entity.RaffleRuleCache;
import cn.lary.module.raffle.entity.RaffleWiningUserNotifyMessage;
import cn.lary.module.raffle.service.RaffleEventService;
import cn.lary.module.raffle.service.RaffleRecordService;
import cn.lary.module.wallet.dto.BatchOutcomeFixTransferDTO;
import cn.lary.module.wallet.dto.BatchOutcomeRandomTransferDTO;
import cn.lary.module.wallet.listener.RaffleBatchRecordMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "lary-raffle-event-close",consumerGroup = "lary-raffle-event-close")
public class RaffleEventCloseListener implements RocketMQListener<RaffleEventAutoCloseMessage> {


    private final EventService eventService;
    private final RaffleEventService raffleEventService;
    private final RaffleCacheComponent raffleCacheComponent;
    private final CacheComponent cacheComponent;
    private final MessageService messageService;
    private final RaffleRecordService raffleRecordService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void onMessage(RaffleEventAutoCloseMessage message) {

        log.info("RaffleEventCloseListener receive message:{}", message.toString());
        long uid = message.getUid();

        RaffleEventCache raffle = raffleCacheComponent.getRaffle(message.getUid());
        if (raffle == null) {
            log.error("raffle close event error,no raffle info,uid:{}",uid);
            return;
        }

        RaffleRuleCache rule = raffleCacheComponent.getRule(uid);
        if (rule == null) {
            log.error("rule close event error,no rule info,uid:{}",uid);
            return;
        }
        int estimate = rule.getStage().get() * rule.getShard();

        List<Long> users;
        if (estimate <= raffle.getNum() * 2) {
            users = cacheComponent.simpleGetWinningUsers(uid, rule.getShard(),raffle.getNum());
        }else {
            List<Integer> shardNums = new ArrayList<>();
            int item = raffle.getNum() / rule.getShard();
            int reminder = raffle.getNum() % rule.getShard();
            for (int i = 0; i < rule.getShard(); i++) {
                shardNums.add(item);
            }
            for (int i = 0; i < reminder; i++) {
                shardNums.set(i,item+1);
            }
            users = cacheComponent.fullGetWinningUsers(uid,shardNums);
        }
        // process raffle
        int category = message.getCategory();
        List<RaffleRecord> data;
        String content = null;
        if (category == LARY.RAFFLE.CUSTOM){
            content = raffle.getContent() + "个数"+ raffle.getItemNum();
        }
        if (category == LARY.RAFFLE.MONEY_FIX) {
            content = "随机金额";
            messageService.asyncSendRocketMessage(new RaffleBatchRecordMessage()
                    .setCategory(category)
                    .setFixTransferDTO(new BatchOutcomeFixTransferDTO()
                            .setRecipients(users)
                            .setCategory(category)
                            .setAmount(raffle.getAmount())
                            .setTotalAmount(raffle.getTotalAmount())
                            .setChannel(raffle.getStreamId())
                            .setType(LARY.RAFFLE.MONEY_RANDOM))
            );
        }

        if (category == LARY.RAFFLE.MONEY_RANDOM) {
            content = "固定金额";
            messageService.asyncSendRocketMessage(new RaffleBatchRecordMessage()
                    .setCategory(category)
                    .setRandomTransfer(new BatchOutcomeRandomTransferDTO()
                            .setUid(uid)
                            .setType(LARY.RAFFLE.MONEY_RANDOM)
                            .setCategory(category)
                            .setChannel(raffle.getStreamId())
                            .setRecipients(users)
                            .setAmount(allocateReward(raffle.getTotalAmount(), raffle.getNum()))
                            .setTotalAmount(raffle.getTotalAmount())));
        }
        String finalContent = content;
        data = users.stream().map(user -> new RaffleRecord()
                .setRaffleId(raffle.getRaffleId())
                .setStreamId(raffle.getStreamId())
                .setContent(finalContent)
                .setToUid(uid)
                .setUid(user)).toList();
        transactionTemplate.executeWithoutResult(status -> {
            raffleRecordService.saveBatch(data);
            String notifyContent = "恭喜您获得以下奖品:["
                    +raffle.getContent()+raffle.getItemNum() +"]";
            messageService.send(new RaffleWiningUserNotifyMessage(raffle.getUid(),users,notifyContent));
            raffleEventService.lambdaUpdate()
                    .set(RaffleEvent::getSyncStatus,LARY.SYNC_STATUS.SUCCESS)
                    .set(RaffleEvent::getRecipients,users)
                    .eq(RaffleEvent::getUid,uid);
            eventService.commit(message.getEventId());
        });
        raffleCacheComponent.dropRaffle(uid);
        raffleCacheComponent.dropRule(uid);;
        log.info("raffle close event success,uid:{},raffleId:{},streamId:{}"
                ,uid,message.getRaffleId(),message.getStreamId());
    }

    public static List<BigDecimal> allocateReward(BigDecimal totalAmount, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        double[] randomNumbers = new double[count];
        double sum = 0;
        for (int i = 0; i < count; i++) {
            double randomValue = ThreadLocalRandom.current().nextGaussian();
            randomNumbers[i] = Math.abs(randomValue);
            sum += randomNumbers[i];
        }
        double[] normalizedNumbers = new double[count];
        for (int i = 0; i < count; i++) {
            normalizedNumbers[i] = randomNumbers[i] / sum;
        }
        List<BigDecimal> allocatedAmounts = new ArrayList<>(count);
        BigDecimal remainingAmount = totalAmount;
        for (int i = 0; i < count - 1; i++) {
            BigDecimal amount = totalAmount.multiply(BigDecimal.valueOf(normalizedNumbers[i])).setScale(2, RoundingMode.HALF_UP);
            allocatedAmounts.add(amount);
            remainingAmount = remainingAmount.subtract(amount);
        }
        allocatedAmounts.add(remainingAmount);
        return allocatedAmounts;
    }
}


