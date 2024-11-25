package cn.lary.raffle.component;

//import cn.lary.module.cache.component.PubSubComponent;
//import cn.lary.module.cache.dto.RaffleRuleNotifyDTO;
import cn.lary.raffle.dto.RaffleEventCache;
import cn.lary.raffle.dto.RaffleRuleCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RaffleCacheComponent {

    String LARY_RAFFLE = "lary:raffle:";
    String LARY_RAFFLE_RULE = "lary:raffle:rule";
    String LARY_RAFFLE_RULE_TOPIC = "lary:raffle:rule:topic";
    String LARY_RAFFLE_EVENT_TOPIC = "lary:raffle:event:topic";

    private final Cache<Long , RaffleEventCache> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    private final Cache<Long , RaffleRuleCache> ruleCache = Caffeine.newBuilder().build();
    private final RedissonClient redisson;
//    private final PubSubComponent pubSubComponent;


    public RaffleEventCache getRaffle(long eid){
        RaffleEventCache cache = this.cache.getIfPresent(eid);
        if(cache != null){
            return cache;
        }
        cache = redisson.<RaffleEventCache>getBucket(LARY_RAFFLE + eid).get();
        return cache;
    }

    public void setRaffle(long eid, RaffleEventCache dto){
        cache.put(eid,dto);
//        pubSubComponent.publishToTopic(LARY_RAFFLE_EVENT_TOPIC,dto);
    }

    public void dropRaffle(long eid){
        cache.invalidate(eid);
    }

    public void setRule(long eid, RaffleRuleCache cache){
        ruleCache.put(eid, cache);
//        pubSubComponent.publishToTopic(LARY_RAFFLE_RULE_TOPIC,cache);
    }
    public RaffleRuleCache getRule(long eid){
        return ruleCache.getIfPresent(eid);
    }
    public void dropRule(long eid){
        ruleCache.invalidate(eid);
    }


    @PostConstruct
    public void subscribeToTopic() {
        // TODO  :  这里
//        pubSubComponent.subscribeToTopic(LARY_RAFFLE_EVENT_TOPIC
//                , (channel, msg) -> {setRaffle(msg.getRid(), msg);}
//                , RaffleEventCache.class);
//        pubSubComponent.subscribeToTopic(LARY_RAFFLE_RULE_TOPIC
//                , (channel, msg) -> {setRule(msg.getEid(),new RaffleRuleCache()
//                        .setLimit(msg.getLimit())
//                        .setShard(msg.getShard()));}
//                , RaffleRuleNotifyDTO.class);

    }

//    public final void sendRuleMessage(RaffleRuleNotifyDTO dto) {
//        pubSubComponent.publishToTopic(LARY_RAFFLE_RULE_TOPIC+dto.getEid(), dto);
//    }
//
//    public final void sendMessage(RaffleEventCache cache){
//        pubSubComponent.publishToTopic(LARY_RAFFLE_EVENT_TOPIC+cache.getEid(), cache);
//    }
}
