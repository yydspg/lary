package cn.lary.engine.intime;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class RankRuleManager {

    private final int UNKNOWN = -1;

    private final int RANK_GAME = 10001;
    private final int RANK_PC = 10002;
    private final int RANK_OUTSIDE = 10003;
    private final int RANK_BEAUTY = 10004;
    private final int RANK_SONG = 10005;
    private final int SUPER = 10005;
    private final int COMMON = 10005;

    private final String LARY_RANK = "lary:rank:";
    private final String LARY_RANK_HOME = "lary:rank:home:";

    private final HashMap<Integer,Integer> tagMap = new HashMap<>();


    @PostConstruct
    public void init(){
        tagMap.put(RANK_GAME,SUPER);
        tagMap.put(RANK_PC,COMMON);
        tagMap.put(RANK_OUTSIDE,SUPER);
        tagMap.put(RANK_BEAUTY,SUPER);
        tagMap.put(RANK_SONG,COMMON);
    }
    /**
     * 查询主页构建home key
     * @param score 分数
     * @return key
     */
    public final String buildHomeKey(double score) {
        if (score <= 500.0) {
            return null;
        }
        if (score > 1000.0) {
            log.error("score over limit");
            return null;
        }
        return LARY_RANK_HOME +getHOME(score) + ":"+ ThreadLocalRandom.current().nextInt(1, 10);
    }


    public final  String buildKey(double score,int tag) {
        Integer policy = tagMap.get(tag);
        if( policy == null) {
            log.error("tag get error,tag:{}",tag);
            return null;
        }
        int index = UNKNOWN;
        if(policy == SUPER) {
            index = getSUPER(score);
        }
        if(policy == COMMON) {
            index = getCOMMON(score);
        }
        if(index == UNKNOWN) {
            log.error("get index error,score:{}",score);
            return null;
        }
        return LARY_RANK + tag + ":" + index;
    }



    private  int getCOMMON(double score) {
        if(score >= 0.0 && score <= 200.0) {
            return 5;
        }
        if(score > 200.0 && score <= 400.0) {
            return 4;
        }
        if(score > 400.0 && score <= 600.0) {
            return 3;
        }
        if(score > 600.0 && score <= 800.0) {
            return 2;
        }
        if(score > 800.0 && score <= 1000.0) {
            return 1;
        }
        return UNKNOWN;
    }

    private int getSUPER(double score) {
        if(score >= 0.0 && score <= 200.0) {
            return 10;
        }
        if(score > 200.0 && score <= 300.0) {
            return 9;
        }
        if(score > 300.0 && score <= 400.0) {
            return 8;
        }
        if(score > 400.0 && score <= 450.0) {
            return 7;
        }
        if(score > 450.0 && score <= 500.0) {
            return 6;
        }
        if(score > 500.0 && score <= 550.0) {
            return 5;
        }
        if(score > 550.0 && score <= 600.0) {
            return 4;
        }
        if(score > 600.0 && score <= 700.0) {
            return 3;
        }
        if(score > 700.0 && score <= 800.0) {
            return 2;
        }
        if(score > 800.0 && score <= 1000.0) {
            return 1;
        }
        return UNKNOWN;
    }
    private int getHOME(double score) {

        if (score > 400.0 && score <= 450.0) {
            return 10;
        }
        if (score > 450.0 && score <= 500.0) {
            return 9;
        }
        if (score > 500.0 && score <= 550.0) {
            return 8;
        }
        if (score > 550.0 && score <= 600.0) {
            return 7;
        }
        if (score > 600.0 && score <= 650.0) {
            return 6;
        }
        if (score > 650.0 && score <= 700.0) {
            return 5;
        }
        if (score > 700.0 && score <= 750.0) {
            return 4;
        }
        if (score > 750.0 && score <= 800.0) {
            return 3;
        }
        if (score > 800.0 && score <= 850.0) {
            return 2;
        }
        if (score > 850.0 && score <= 1000.0) {
            return 1;
        }
        return UNKNOWN;
    }

}
