package cn.lary.module.redpacket.component;

import cn.lary.module.cache.component.PubSubComponent;
import cn.lary.module.cache.dto.RedpacketRuleNotifyDTO;
import cn.lary.module.redpacket.dto.RedpacketEventCache;
import cn.lary.module.redpacket.dto.RedpacketRuleCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedpacketCacheComponent {

    String LARY_REDPACKET = "lary:redpacket:";
    String LARY_REDPACKET_TOPIC = "lary:redpacket:topic";
    String LARY_REDPACKET_RULE = "lary:redpacket:rule";
    String LARY_REDPACKET_RULE_TOPIC = "lary:redpacket:rule:topic";
    private final Cache<Long , RedpacketEventCache> raffleCache = Caffeine.newBuilder().build();
    private final Cache<Long , RedpacketRuleCache> raffleRuleCache = Caffeine.newBuilder().build();
    private final RedissonClient redisson;
    private final PubSubComponent pubSubComponent;

    public RedpacketEventCache getRedpacket(long eid){
        return raffleCache.getIfPresent(eid);
    }

    public void setRedpacket(long eid, RedpacketEventCache cache){
        raffleCache.put(eid, cache);
    }

    public void dropRedpacket(long eid){
        raffleCache.invalidate(eid);
    }

    public void setRule(long eid, RedpacketRuleCache cache){
        raffleRuleCache.put(eid, cache);
    }

    public RedpacketRuleCache getRule(long eid){
       return raffleRuleCache.getIfPresent(eid);
    }

    public void dropRule(long eid){
        raffleRuleCache.invalidate(eid);
    }
    @PostConstruct
    public void subscribeToTopic() {
        // TODO  :  +1
        pubSubComponent.subscribeToTopic(LARY_REDPACKET_TOPIC
                , (channel, msg) -> {setRedpacket(msg.getEid(), msg);}
                , RedpacketEventCache.class);
        pubSubComponent.subscribeToTopic(LARY_REDPACKET_RULE_TOPIC
                , (channel, msg) -> {setRule(msg.getEid(),new RedpacketRuleCache()
                        .setCategory(msg.getCategory())
                        .setLimit(new AtomicInteger(msg.getLimit())));}
                , RedpacketRuleNotifyDTO.class);
    }
    public final void sendRuleMessage(RedpacketRuleNotifyDTO dto) {
        pubSubComponent.publishToTopic(LARY_REDPACKET_RULE_TOPIC+dto.getEid(), dto);
    }
    
    public final void sendMessage(RedpacketEventCache cache){
        pubSubComponent.publishToTopic(LARY_REDPACKET_TOPIC+cache.getEid(), cache);
    }
}
