package cn.lary.module.cache.component;

import cn.lary.module.cache.dto.RaffleRuleNotifyDTO;
import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.raffle.entity.RaffleRuleCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RaffleCacheComponent {

    String LARY_RAFFLE = "lary:raffle:";
    String LARY_RAFFLE_RULE = "lary:raffle:rule";
    String LARY_RAFFLE_RULE_TOPIC = "lary:raffle:rule:topic";
    String LARY_RAFFLE_EVENT_TOPIC = "lary:raffle:event:topic";
    private final Cache<Long , RaffleEventCache> cache = Caffeine.newBuilder().build();
    private final Cache<Long , RaffleRuleCache> ruleCache = Caffeine.newBuilder().build();
    private final RedissonClient redisson;
    private final PubSubComponent pubSubComponent;


    public RaffleEventCache getRaffle(long uid){
        RaffleEventCache cache = this.cache.getIfPresent(uid);
        if(cache != null){
            return cache;
        }
        cache = redisson.<RaffleEventCache>getBucket(LARY_RAFFLE + uid).get();
        return cache;
    }

    public void setRaffle(long uid, RaffleEventCache dto){
        cache.put(uid,dto);
        pubSubComponent.publishToTopic(LARY_RAFFLE_EVENT_TOPIC,dto);
    }

    public void dropRaffle(long uid){
        cache.invalidate(uid);
    }
    public void setRule(long uid, RaffleRuleCache cache){
        ruleCache.put(uid, cache);
        pubSubComponent.publishToTopic(LARY_RAFFLE_RULE_TOPIC,cache);
    }
    public RaffleRuleCache getRule(long uid){
        return ruleCache.getIfPresent(uid);
    }
    public void dropRule(long uid){
        ruleCache.invalidate(uid);
    }


    @PostConstruct
    public void subscribeToTopic() {
        pubSubComponent.subscribeToTopic(LARY_RAFFLE_EVENT_TOPIC
                , (channel, msg) -> {setRaffle(msg.getUid(), msg);}
                , RaffleEventCache.class);
        pubSubComponent.subscribeToTopic(LARY_RAFFLE_RULE_TOPIC
                , (channel, msg) -> {setRule(msg.getUid(),new RaffleRuleCache()
                        .setLimit(msg.getLimit())
                        .setShard(msg.getShard()));}
                , RaffleRuleNotifyDTO.class);
    }
    public final void sendRuleMessage(RaffleRuleNotifyDTO dto) {
        pubSubComponent.publishToTopic(LARY_RAFFLE_RULE_TOPIC+dto.getUid(), dto);
    }
    public final void sendMessage(RaffleEventCache cache){
        pubSubComponent.publishToTopic(LARY_RAFFLE_EVENT_TOPIC+cache.getUid(), cache);
    }
}
