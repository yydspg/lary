package cn.lary.redpacket.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.redpacket.component.RedpacketCacheComponent;
import cn.lary.api.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.api.redpacket.dto.RedpacketEventCache;
import cn.lary.api.redpacket.entity.RedpacketEvent;
import cn.lary.redpacket.mapper.RedpacketEventMapper;
import cn.lary.redpacket.service.RedpacketEventService;
import cn.lary.api.redpacket.vo.RedpacketEventVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedpacketEventServiceImpl extends ServiceImpl<RedpacketEventMapper, RedpacketEvent> implements RedpacketEventService {

    private final RedpacketCacheComponent redpacketCacheComponent;
//    private final LiveCacheComponent liveCacheComponent;
//    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;
//    private final WalletService walletService;
//    private final MessageService messageService;

    @Override
    public ResponsePair<Void> redpacket(RedpacketEventBuildDTO dto) {
//        long uid = RequestContext.uid();
//        LiveCache live = liveCacheComponent.getLive(uid);
//        if (live == null) {
//            return BusinessKit.when("no live info");
//        }
//        RedpacketEventCache data = redpacketCacheComponent.getRedpacket(uid);
//        if(data != null){
//            log.error("have unfinished red packet service,uid:{}", uid);
//            return BusinessKit.when("have unfinished redpacket event");
//        }
//        String badWord = SensitiveWordHelper.findFirst(dto.getTitle());
//        if (StringKit.isNotEmpty(badWord)) {
//            return BusinessKit.when("title has bad word:"+badWord);
//        }
//        boolean needSendMessage = StringKit.isEmpty(dto.getMessage());
//        if (needSendMessage) {
//            badWord = SensitiveWordHelper.findFirst(dto.getMessage());
//            if (StringKit.isNotEmpty(badWord)) {
//                return BusinessKit.when("message has bad word:"+badWord);
//            }
//        }
//        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
//            return BusinessKit.when("amount less than 0");
//        }
//        if (dto.getTotalAmount()
//                .compareTo(dto.getAmount()
//                        .multiply(new BigDecimal(dto.getNum()))) != 0) {
//            return BusinessKit.when("total amount not match amount multiply num");
//        }
//        if (live.getLevel() >= 5){
//            processSuperEvent();
//        }else {
//            processEvent();
//        }
//        Wallet wallet = walletService.lambdaQuery()
//                .select(Wallet::getPocket)
//                .select(Wallet::getStatus)
//                .eq(Wallet::getUid, uid)
//                .one();
//        if(wallet == null || wallet.getStatus() != WALLET.STATUS.COMMON){
//            log.error("create redpacket error,wallet not found or wallet status error,uid:{}", uid);
//            return BusinessKit.when("wallet status error");
//        }
//        BigDecimal amount = calculate(dto);
//        if (wallet.getPocket().compareTo(amount) < 0 ) {
//            return BusinessKit.when("零钱不足,请充值");
//        }
//        walletService.lambdaUpdate()
//                .eq(Wallet::getUid, uid)
//                .setDecrBy(Wallet::getPocket,amount)
//                .setIncrBy(Wallet::getOutcome,amount);
//        RedpacketEvent event = new RedpacketEvent()
//                .setNum(dto.getNum())
//                .setUid(uid)
//                .setTitle(dto.getTitle())
//                .setDuration(dto.getDuration())
//                .setStream(live.getSid())
//                .setStartAt(dto.getStartAt());
//        save(event);
//        long eventId = eventService.begin(new RedPacketEventDTO(uid, live.getSid(), event.getId()));
//        redpacketCacheComponent.setRedpacket(uid,new RedpacketEventCache()
//                .setAmount(dto.getAmount())
//                .setAmount(amount)
//                .setTitle(dto.getTitle())
//                .setNum(dto.getNum())
//                .setStartAt(dto.getStartAt())
//                .setStream(live.getSid()));
//        redpacketCacheComponent.setRule(uid,new RedpacketRuleCache()
//                .setCategory(););
//        messageService.asyncSendRocketMessage(new RedpacketLocalRuleMessage()
//                .setCategory(REDPACKET.CATEGORY.SUPER)
//                .setLimit(2)
//                .setUid(uid));
//        messageService.asyncSendRocketMessage(new RedpacketEventAutoCloseMessage()
//                        .setUid(uid)
//                        .setEventId(eventId)
//                        .setStreamId(live.getSid())
//                        .setRedPacketId(event.getId()));
//        messageService.send(new RedpacketBuildNotifyDTO(uid, live.getSid(), eventId));
//        return BusinessKit.ok();
        return null;
    }

    @Override
    public ResponsePair<RedpacketEventVO> info(long toUid) {

        RedpacketEventCache cache = redpacketCacheComponent.getRedpacket(toUid);
        if (cache == null) {
            return BusinessKit.fail("no redpacket info");
        }
        return BusinessKit.ok(new RedpacketEventVO(cache));
    }

    private BigDecimal calculate(RedpacketEventBuildDTO dto) {
        return dto.getAmount()
                .multiply(new BigDecimal(dto.getNum()));
    }

    private void processSuperEvent(){

    }
    private void processEvent(){

    }
}
