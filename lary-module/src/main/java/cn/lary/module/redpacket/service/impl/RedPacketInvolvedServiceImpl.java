package cn.lary.module.redpacket.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.JSONKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.cache.component.RedpacketCacheComponent;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.redpacket.dto.RedpacketFsyncDTO;
import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;
import cn.lary.module.redpacket.entity.RedpacketTokenDTO;
import cn.lary.module.redpacket.service.RedPacketInvolvedService;
import cn.lary.module.redpacket.vo.RedpacketTokenVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedPacketInvolvedServiceImpl implements RedPacketInvolvedService {



    private final RedpacketCacheComponent redpacketCacheComponent;
    private final CacheComponent cacheComponent;

    
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
        return process(event,getRemind(rule,toUid));
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
        if(CollectionKit.isEmpty(tokens)){
            return BusinessKit.fail("token decrypt error");
        }

        return BusinessKit.ok();
    }

    private int getRemind(RedpacketRuleCache rule,long toUid) {
        return switch (rule.getCategory()) {
            case LARY.REDPACKET.CATEGORY.COMMON -> redpacketCacheComponent.getRule(toUid).getLimit().getAndDecrement();
            case LARY.REDPACKET.CATEGORY.SUPER -> cacheComponent.decrRedpacketCount(toUid);
            default -> 0;
        };
    }
    private ResponsePair<RedpacketTokenVO> process(RedpacketEventCache event,int remind) {
        if (remind <= 0) {
            return BusinessKit.ok(new RedpacketTokenVO()
                    .setStatus(LARY.REDPACKET.STATUS.FAIL)
                    .setStreamId(event.getStream()));
        }
        RedpacketTokenDTO dto = new RedpacketTokenDTO()
                .setAmount(event.getAmount())
                .setUid(RequestContext.getLoginUID())
                .setStreamId(event.getStream());
        return BusinessKit.ok(new RedpacketTokenVO()
                .setStatus(LARY.REDPACKET.STATUS.INIT)
                .setStreamId(event.getStream())
                .setToken(token(dto)));
    }
    private String token(RedpacketTokenDTO dto) {
        return StringKit.encrypt(JSONKit.toJSON(dto));
    }
}
