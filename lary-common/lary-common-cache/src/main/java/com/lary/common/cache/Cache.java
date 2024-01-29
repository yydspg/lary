package com.lary.common.cache;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author paul 2023/12/30
 */

public interface Cache<T> {
    /**
     * @param pattern: 匹配模式
     * @return List<Object>
     * @author paul
     * @description 模糊匹配key
     * @date 2023/12/30 3:22
     */
    List<Object> keys(String pattern);
    boolean remove(Object key);
    void vagueDel(Object key);
    void clear();

    void put(Object k,T t,long time);
    void put(Object k,T t);
    void putHash(Object k,Object f,Object v);
    void putAllHash(Object k,Map map);

    List multiGet(Collection keys);
    T get(Object k);
    T getHash(Object k,Object f);
    Map<Object,Object> getHash(Object k);
    String getString(Object k);
    boolean hasKey(Object k);


    Long incr(String k, long liveTime);
    Long incr(String key);
    /*
     Deduplication statistics
     el means element
     pfadd k [el [el ...]]#add all el into HyerLogLog data structure-->estimation of the number of unique items

     pfcount k #return the approximated cardinality

     pfmerge destk k [k ...] #merger all k ,and return the estimation unique items of destk
   */
    /**
     * @param k: key
     * @param v: el
     * @return Long
     * @author paul
     * @description pfadd
     * @date 2023/12/30 3:43
     */
    Long cumulative(Object k, Object v);
    /**
     * @param k: key
     * @return Long
     * @author paul
     * @description pfcount
     * @date 2023/12/30 3:44
     */
    Long counter(Object k);

    /*
        #s means score m means member k means key
        zadd k s m [s m ...] #adds all the specified members with the specified score
        #to the sorted set stored at k.if an m has already exists ,update and reinsert
        # to ensure the correct ordering
        zcard k # return sorted set cardinality (number of elements)

        zrem k m [m ...] #remover the specified m from k

        zincrby k increment m # inbcrement the score of m in k by increment

        zrange k b e # return the specified range of elements in k based on score size

        zrevrange k b e # according to zrange command,return remaining elements

        zscore k m #return m's score in k

        zrangebyscore min max [WITHSCORES] [LIMIT offset count]
        #return count elements in k with the score between min and max (including ==)

        zcount k min max #return number of elements in k with the score between min and max

        zrank k v #return the rank of member in k

        zrevrank k v #obtain reverse index

        zmpop numkeys k [k ...]<MIN | MAX> {COUNT count}
        # the number of k1, k2 ,k3 == numkeys
        #count = the number of every k return element(member + score)
        #MIN means return from head to tail ,MAX else
     */
    /**
     * 移除 zSet队列值
     *
     * @param k   key
     * @param m  remove member
     * @return 删除数量
     */
    Long zRemove(String k, String... m);
    /**
     * 获取 某key 下 某一分值区间的队列
     *
     * @param k  缓存key
     * @param from 开始时间
     * @param to   结束时间
     * @return 数据
     */
    Set<ZSetOperations.TypedTuple<Object>> zRangeByScore(String k, int from, long to);
    /**
     * @param k: key
     * @param s: score
     * @param m: member
     * @return boolean
     * @author paul
     * @description 向zSet添加成员
     * @date 2023/12/30 3:35
     */
    boolean zAdd(String k, long s, String m);
    /**
     * @param k: key
     * @param c: 查询数量
     * @return Set<TypedTuple<Object>>
     * @author paul
     * @description  ZREVERANGE命令, 查询Sorted Set中指定范围的值
     * @date 2023/12/30 3:37
     */
    Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String k, Integer c);
    Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String k, Integer s, Integer e);

    void incrementScore(String k, String m);
    void incrementScore(String k, String m, Integer score);

    /*
    #o means offset,v means value k means key
    setbit k o v #set the bit at offset with v

    getbit k o #return the bit v at offset in k

    strlen k # 扩容以字节为单位 8bit

    bitcount k #  return count of members which equals 1

    bitop <AND | OR | XOR | NOT> destkey k [k ...]
    # destkey == output k
    # op on k1 k2 ...位运算
     */
    void setBit(Object k,long o,boolean v);
    boolean getBit(Object k,long o);
}
