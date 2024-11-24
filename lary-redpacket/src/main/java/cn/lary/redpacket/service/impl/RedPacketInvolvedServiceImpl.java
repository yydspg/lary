package cn.lary.redpacket.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.JSONKit;
import cn.lary.common.kit.StringKit;
import cn.lary.redpacket.component.RedpacketCacheComponent;
import cn.lary.redpacket.constant.REDPACKET;
import cn.lary.redpacket.dto.RedpacketEventCache;
import cn.lary.redpacket.dto.RedpacketFsyncDTO;
import cn.lary.redpacket.dto.RedpacketRuleCache;
import cn.lary.redpacket.dto.RedpacketTokenDTO;
import cn.lary.redpacket.entity.RedpacketRecord;
import cn.lary.redpacket.service.RedPacketInvolvedService;
import cn.lary.redpacket.service.RedpacketRecordService;
import cn.lary.redpacket.vo.RedpacketTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedPacketInvolvedServiceImpl implements RedPacketInvolvedService {

    private final RedpacketCacheComponent redpacketCacheComponent;
//    private final CacheComponent cacheComponent;
//    private final MessageService messageService;
    private final LaryIDBuilder builder;
    private final RedpacketRecordService redpacketRecordService;
    private final TransactionTemplate transactionTemplate;

    
    @Override
    public ResponsePair<RedpacketTokenVO> join(long toUid) {
        RedpacketEventCache event = redpacketCacheComponent.getRedpacket(toUid);
        if (event == null) {
            return BusinessKit.fail("no redpacket event");
        }
        RedpacketRuleCache rule = redpacketCacheComponent.getRule(toUid);
        if (rule == null) {
            return BusinessKit.fail("no redpacket rule");
        }
        ResponsePair<RedpacketTokenVO> pair = process(event, getRemind(rule, toUid));
        if (!pair.isFail()) {
//            messageService.asyncSendRocketMessage(new RedpacketRecordMessage()
//                    .setToken(pair.getData().getToken()));
        }
        return pair;
    }

    @Override
    public ResponsePair<Void> fsync(RedpacketFsyncDTO dto) {

        List<String> items = dto.getItems();
        if(CollectionKit.isEmpty(items)){
            return BusinessKit.fail("no items");
        }
        List<String> tokens = items.stream().map(t -> {
            try {
                return StringKit.decrypt(t);
            } catch (Exception e) {
                log.error("decrypt redpacket error,message:{}",e.getMessage());
                return null;
            }
        }).filter(StringKit::isNotEmpty).toList();
        if(CollectionKit.isNotEmpty(tokens)){
            List<RedpacketTokenDTO> data = tokens.stream()
                    .map(t -> JSONKit.fromJSON(t, RedpacketTokenDTO.class))
                    .toList();
            if (CollectionKit.isNotEmpty(data)) {
                List<RedpacketRecord> store = data.stream().map(RedpacketRecord::new).toList();
                if (CollectionKit.isNotEmpty(store)) {
                    transactionTemplate.executeWithoutResult(status -> {
                        redpacketRecordService.saveBatch(store);
                    });
                }
            }
        }
        return BusinessKit.ok();
    }

    private int getRemind(RedpacketRuleCache rule,long toUid) {
        return switch (rule.getCategory()) {
            case REDPACKET.CATEGORY.COMMON -> redpacketCacheComponent.getRule(toUid).getLimit().getAndDecrement();
//            case REDPACKET.CATEGORY.SUPER -> cacheComponent.decrRedpacketCount(toUid);
            default -> 0;
        };
    }
    private ResponsePair<RedpacketTokenVO> process(RedpacketEventCache event,int remind) {
        if (remind <= 0) {
            return BusinessKit.ok(new RedpacketTokenVO()
                    .setStatus(REDPACKET.STATUS.FAIL)
                    .setStreamId(event.getSid()));
        }
        RedpacketTokenDTO dto = new RedpacketTokenDTO()
                .setAmount(event.getAmount())
                .setUid(RequestContext.uid())
                .setSid(event.getSid())
                .setRid(builder.next());
        return BusinessKit.ok(new RedpacketTokenVO()
                .setStatus(REDPACKET.STATUS.INIT)
                .setStreamId(event.getSid())
                .setToken(token(dto)));
    }
    private String token(RedpacketTokenDTO dto) {
        return StringKit.encrypt(JSONKit.toJSON(dto));
    }
}
