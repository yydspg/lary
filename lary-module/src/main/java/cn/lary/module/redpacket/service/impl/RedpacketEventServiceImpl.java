package cn.lary.module.redpacket.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.common.service.EventService;
import cn.lary.module.cache.component.LiveCacheComponent;
import cn.lary.module.cache.component.RedpacketCacheComponent;
import cn.lary.module.cache.dto.LiveCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.dto.RedPacketEventDTO;
import cn.lary.module.redpacket.dto.RedpacketBuildNotifyDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.module.redpacket.entity.RedpacketEvent;
import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.mapper.RedpacketEventMapper;
import cn.lary.module.redpacket.service.RedpacketEventService;
import cn.lary.module.redpacket.listener.RedpacketEventAutoCloseMessage;
import cn.lary.module.redpacket.vo.RedpacketEventVO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
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
    private final LiveCacheComponent liveCacheComponent;
    private final EventService eventService;
    private final TransactionTemplate transactionTemplate;
    private final WalletService walletService;
    private final MessageService messageService;

    @Override
    public ResponsePair<Void> redpacket(RedpacketEventBuildDTO dto) {
        long uid = RequestContext.uid();
        LiveCache live = liveCacheComponent.getLive(uid);
        if (live == null) {
            return BusinessKit.fail("no live info");
        }
        RedpacketEventCache data = redpacketCacheComponent.getRedpacket(uid);
        if(data != null){
            log.error("have unfinished red packet service,uid:{}", uid);
            return BusinessKit.fail("have unfinished redpacket event");
        }
        String badWord = SensitiveWordHelper.findFirst(dto.getTitle());
        if (StringKit.isNotEmpty(badWord)) {
            return BusinessKit.fail("title has bad word:"+badWord);
        }
        boolean needSendMessage = StringKit.isEmpty(dto.getMessage());
        if (needSendMessage) {
            badWord = SensitiveWordHelper.findFirst(dto.getMessage());
            if (StringKit.isNotEmpty(badWord)) {
                return BusinessKit.fail("message has bad word:"+badWord);
            }
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BusinessKit.fail("amount less than 0");
        }
        if (dto.getTotalAmount()
                .compareTo(dto.getAmount()
                        .multiply(new BigDecimal(dto.getNum()))) != 0) {
            return BusinessKit.fail("total amount not match amount multiply num");
        }
        if (live.getLevel() >= 5){
            processSuperEvent();
        }else {
            processEvent();
        }
        Wallet wallet = walletService.lambdaQuery()
                .select(Wallet::getPocket)
                .select(Wallet::getStatus)
                .eq(Wallet::getUid, uid)
                .one();
        if(wallet == null || wallet.getStatus() != LARY.WALLET.STATUS.COMMON){
            log.error("create redpacket error,wallet not found or wallet status error,uid:{}", uid);
            return BusinessKit.fail("wallet status error");
        }
        BigDecimal amount = calculate(dto);
        if (wallet.getPocket().compareTo(amount) < 0 ) {
            return BusinessKit.fail("零钱不足,请充值");
        }
        walletService.lambdaUpdate()
                .eq(Wallet::getUid, uid)
                .setDecrBy(Wallet::getPocket,amount)
                .setIncrBy(Wallet::getOutcome,amount);
        RedpacketEvent event = new RedpacketEvent()
                .setNum(dto.getNum())
                .setUid(uid)
                .setTitle(dto.getTitle())
                .setDuration(dto.getDuration())
                .setStream(live.getStreamId())
                .setStartAt(dto.getStartAt());
        save(event);
        long eventId = eventService.begin(new RedPacketEventDTO(uid, live.getStreamId(), event.getId()));
//        redpacketCacheComponent.setRedpacket(uid,new RedpacketEventCache()
//                .setAmount(dto.getAmount())
//                .setAmount(amount)
//                .setTitle(dto.getTitle())
//                .setNum(dto.getNum())
//                .setStartAt(dto.getStartAt())
//                .setStream(live.getStreamId()));
//        redpacketCacheComponent.setRule(uid,new RedpacketRuleCache()
//                .setCategory(););
//        messageService.asyncSendRocketMessage(new RedpacketLocalRuleMessage()
//                .setCategory(LARY.REDPACKET.CATEGORY.SUPER)
//                .setLimit(2)
//                .setUid(uid));
        messageService.asyncSendRocketMessage(new RedpacketEventAutoCloseMessage()
                        .setUid(uid)
                        .setEventId(eventId)
                        .setStreamId(live.getStreamId())
                        .setRedPacketId(event.getId()));
        messageService.send(new RedpacketBuildNotifyDTO(uid, live.getStreamId(), eventId));
        return BusinessKit.ok();
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
