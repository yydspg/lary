package cn.lary.module.common.cache;

import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface CacheComponent {
      /*
        strings
     */


    String get(String k);

    void set(String k,String v);

    void set(String k, String v, Long exp);

    void set(String k, String v, Long exp, TimeUnit timeUnit);

    void delete(String k);

    /*
        hash
     */

    void setHash(String k,String f,String v);
    void incrHash(String k,String f);
    void decrHash(String k,String f);
    void setHash(String k,Map map);
    void setHash(String k,Map map,Long exp);
    void setHash(String k,Map map,Long exp,TimeUnit timeUnit);
    String getHash(String k,String f);
    Map<Object,Object> getHash(String k);


    void append(String k,String v);
    void append(String k,Collection<String> v);

    /**
     * expire
     */
    void renewal(String k,long exp);
    void renewal(String k,long exp,TimeUnit timeUnit);

    <T> T execute(RedisScript<T> script, List<String> keys, Object ... args);

    /**
     * 抽奖人数少时获取
     * @param uid u
     * @param shardNum s
     * @param num n
     * @return uids
     */
    List<Long>  simpleGetWinningUsers(long uid, int shardNum,int num);


    List<Long> fullGetWinningUsers(long uid, List<Integer> shardsCount);


    void appendInvolvedUsers(long uid, List<Long> users);


    int decrRedpacketCount(long uid);

}
