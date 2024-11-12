package cn.lary.module.cache.component;

import cn.lary.module.cache.dto.RedpacketRuleNotifyDTO;
import cn.lary.module.redpacket.entity.RedpacketEventCache;
import cn.lary.module.redpacket.entity.RedpacketRuleCache;
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

    public RedpacketEventCache getRedpacket(long uid){
        return raffleCache.getIfPresent(uid);
    }

    public void setRedpacket(long uid, RedpacketEventCache cache){
        raffleCache.put(uid, cache);
    }

    public void dropRedpacket(long uid){
        raffleCache.invalidate(uid);
    }

    public void setRule(long uid, RedpacketRuleCache cache){
        raffleRuleCache.put(uid, cache);
    }

    public RedpacketRuleCache getRule(long uid){
       return raffleRuleCache.getIfPresent(uid);
    }

    public void dropRule(long uid){
        raffleRuleCache.invalidate(uid);
    }
    @PostConstruct
    public void subscribeToTopic() {
        pubSubComponent.subscribeToTopic(LARY_REDPACKET_TOPIC
                , (channel, msg) -> {setRedpacket(msg.getUid(), msg);}
                , RedpacketEventCache.class);
        pubSubComponent.subscribeToTopic(LARY_REDPACKET_RULE_TOPIC
                , (channel, msg) -> {setRule(msg.getUid(),new RedpacketRuleCache()
                        .setCategory(msg.getCategory())
                        .setLimit(new AtomicInteger(msg.getLimit())));}
                , RedpacketRuleNotifyDTO.class);
    }
    public final void sendRuleMessage(RedpacketRuleNotifyDTO dto) {
        pubSubComponent.publishToTopic(LARY_REDPACKET_RULE_TOPIC+dto.getUid(), dto);
    }
    public final void sendMessage(RedpacketEventCache cache){
        pubSubComponent.publishToTopic(LARY_REDPACKET_TOPIC+cache.getUid(), cache);
    }
}
