package cn.lary.module.engine.intime;

import cn.lary.common.kit.StringKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.Locale;

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
            batch.<RankNode>getScoredSortedSet(key + ":" + 1).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 2).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 3).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 4).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 5).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 6).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 7).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 8).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 9).addAsync(score, node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 10).addAsync(score, node);
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
            batch.<RankNode>getScoredSortedSet(key + ":" + 1).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 2).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 3).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 4).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 5).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 6).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 7).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 8).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 9).removeAsync(node);
            batch.<RankNode>getScoredSortedSet(key + ":" + 10).removeAsync(node);
            batch.execute();
        }catch (Exception e){
            log.error("batch remove stream key error,when batch  event:{}",e.getMessage());
        }
    }
}
