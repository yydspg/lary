package cn.lary.engine.intime;

import cn.lary.common.kit.StringKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankGroupManager {

    private final RankRuleManager rankRuleManager;
    private final RedissonClient redisson;


    public final void whenUp(StreamEvent event){

        if (event == null ){
            log.error("stream up event empty");
            return;
        }
        double score = event.getScore();
        String key = rankRuleManager.buildKey(score, event.getTag());
        if(key == null){
            log.error("build stream key error,when up event");
            return;
        }
        redisson.<RankNode>getScoredSortedSet(key).add(score, new RankNode(event));


    }

    public final void whenDown(StreamEvent event){
        if (event == null ){
            log.error("stream down event empty");
            return;
        }
        String key = rankRuleManager.buildKey(event.getScore(), event.getTag());
        if(StringKit.isEmpty(key)){
            log.error("build stream key error,when down event");
            return;
        }
        redisson.<RankNode>getScoredSortedSet(key).remove(new RankNode(event));
    }

    private  void batchAdd(String key,RankNode node,double score){
        if(StringKit.isEmpty(key)){
            log.error("stream key empty,when batch add event");
            return;
        }
        try {
            RBatch batch = redisson.createBatch();
            for(int i = 1;i <= 10;i++){
                batch.<RankNode>getScoredSortedSet(key + ":" + i).addAsync(score, node);
            }
            batch.execute();
        }catch (Exception e){
            log.error("batch add  stream key error,when batch event:{}",e.getMessage());
        }
    }
    private  void batchRemove(String key,RankNode node,double score){
        if(StringKit.isEmpty(key)){
            log.error("stream key empty,when batch remove event");
            return;
        }
        try {
            RBatch batch = redisson.createBatch();
            for (int i = 1; i <= 10; i++) {
                batch.<RankNode>getScoredSortedSet(key + ":" + i).removeAsync(node);
            }
            batch.execute();
        }catch (Exception e){
            log.error("batch remove stream key error,when batch  event:{}",e.getMessage());
        }
    }
}
